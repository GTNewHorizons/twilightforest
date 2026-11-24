package twilightforest.client.renderer.entity;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import javax.imageio.ImageIO;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;

import com.mojang.authlib.GameProfile;

import lain.mods.skinport.init.forge.ClientProxy;
import lain.mods.skins.api.SkinProviderAPI;
import lain.mods.skins.api.interfaces.ISkin;
import lain.mods.skins.impl.PlayerProfile;
import lain.mods.skins.impl.SkinData;

public class RenderTFGiantSkinportIntegration {

    private static final int MAX_CACHED_SKINS = 128;
    private static final Map<UUID, CachedSkin> SKIN_CACHE = new LinkedHashMap<>(128, 0.75F, true) {

        @Override
        protected boolean removeEldestEntry(Map.Entry<UUID, CachedSkin> eldest) {
            if (size() <= MAX_CACHED_SKINS) {
                return false;
            }
            Minecraft.getMinecraft().getTextureManager().deleteTexture(eldest.getValue().texture);
            return true;
        }
    };

    private static final class CachedSkin {

        private final ISkin source;
        private final int dataHash;
        private final int dataLength;
        private final ResourceLocation texture;

        private CachedSkin(ISkin source, int dataHash, int dataLength, ResourceLocation texture) {
            this.source = source;
            this.dataHash = dataHash;
            this.dataLength = dataLength;
            this.texture = texture;
        }
    }

    public static ResourceLocation getSkin(EntityClientPlayerMP player, ResourceLocation skin,
            ResourceLocation textureLoc) {
        GameProfile profile = player.getGameProfile();
        if (profile == null || profile.getId() == null) {
            return skin;
        }
        ResourceLocation boundSkin = ClientProxy.bindTexture(profile, skin);
        ISkin source = SkinProviderAPI.SKIN.getSkin(PlayerProfile.wrapGameProfile(profile));
        if (source == null || !source.isDataReady()) {
            return boundSkin;
        }

        ByteBuffer data = source.getData();
        if (data == null) {
            return boundSkin;
        }

        ByteBuffer imageBuffer = data.asReadOnlyBuffer();
        imageBuffer.rewind();
        // Skinport may return new buffer views; compare their contents before decoding an image.
        int dataLength = imageBuffer.remaining();
        int dataHash = imageBuffer.hashCode();
        imageBuffer.rewind();
        CachedSkin cached = SKIN_CACHE.get(profile.getId());
        if (cached != null && cached.source == source
                && cached.dataHash == dataHash
                && cached.dataLength == dataLength) {
            return cached.texture;
        }

        try {
            try (InputStream in = SkinData.wrapByteBufferAsInputStream(imageBuffer)) {
                BufferedImage image = ImageIO.read(in);
                if (image == null) {
                    return boundSkin;
                }
                if (image.getWidth() == image.getHeight()) {
                    image = image.getSubimage(0, 0, image.getWidth(), image.getHeight() / 2);
                }
                ResourceLocation location = Minecraft.getMinecraft().getTextureManager()
                        .getDynamicTextureLocation("tf_giant_skin_" + profile.getId(), new DynamicTexture(image));
                if (cached != null) {
                    Minecraft.getMinecraft().getTextureManager().deleteTexture(cached.texture);
                }
                SKIN_CACHE.put(profile.getId(), new CachedSkin(source, dataHash, dataLength, location));
                return location;
            }
        } catch (Exception e) {
            return cached != null ? cached.texture : textureLoc;
        }
    }

    public static boolean isSlim(EntityClientPlayerMP player) {
        if (player == null || player.getGameProfile() == null) {
            return false;
        }
        ISkin skin = SkinProviderAPI.SKIN.getSkin(PlayerProfile.wrapGameProfile(player.getGameProfile()));
        return skin != null && "slim".equals(skin.getSkinType());
    }
}
