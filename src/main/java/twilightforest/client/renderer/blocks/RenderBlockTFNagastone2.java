package twilightforest.client.renderer.blocks;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import twilightforest.block.BlockTFNagastone2;
import twilightforest.block.BlockTFNagastone2.Direction;
import twilightforest.block.BlockTFNagastone2.Yaw;
import twilightforest.tileentity.TileEntityTFNagastone;

public class RenderBlockTFNagastone2 implements ISimpleBlockRenderingHandler {

    final int renderID;

    public RenderBlockTFNagastone2(int nagastoneRenderID) {
        this.renderID = nagastoneRenderID;
    }

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
        renderInvNormalBlock(renderer, block, metadata);
        restoreRendererRotate(renderer);
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId,
            RenderBlocks renderer) {
        RenderRotatedBlock(renderer, (BlockTFNagastone2) block, x, y, z);
        return true;
    }

    private void restoreRendererRotate(RenderBlocks renderer) {
        renderer.uvRotateSouth = 0;
        renderer.uvRotateEast = 0;
        renderer.uvRotateWest = 0;
        renderer.uvRotateNorth = 0;
        renderer.uvRotateTop = 0;
        renderer.uvRotateBottom = 0;
    }

    private void RenderRotatedBlock(RenderBlocks renderer, BlockTFNagastone2 block, int x, int y, int z) {
        World world = Minecraft.getMinecraft().theWorld;
        int meta = world.getBlockMetadata(x, y, z);
        block.Check4Neighbours(world, x, y, z);
        TileEntityTFNagastone te = (TileEntityTFNagastone) world.getTileEntity(x, y, z);
        Direction dir = block.DirectionFromMeta(meta);
        Yaw face = block.FacingFromMeta(meta);

        IIcon iconXPos = block.getIcon(2, meta);
        IIcon iconXNeg = block.getIcon(3, meta);
        IIcon iconZPos = block.getIcon(5, meta);
        IIcon iconZNeg = block.getIcon(4, meta);
        IIcon iconYPos = block.getIcon(1, meta);
        IIcon iconYNeg = block.getIcon(0, meta);

        // heads
        if (((BlockTFNagastone2) block).isHead) {
            switch (dir) {
                case DOWN:
                    switch (face) {
                        default:
                        case EAST:
                            renderer.uvRotateEast = 1;
                            renderer.uvRotateWest = 2;
                            renderer.uvRotateSouth = 1;
                            renderer.uvRotateNorth = 2;
                            renderer.uvRotateTop = 2;
                            renderer.uvRotateBottom = 1;
                            iconXPos = block.BOTTOM_TIP;
                            iconXNeg = block.TOP_TIP;
                            iconZPos = block.FACE_LEFT;
                            iconZNeg = block.FACE_LEFT;
                            break;
                        case WEST:
                            renderer.uvRotateEast = 2;
                            renderer.uvRotateWest = 1;
                            renderer.uvRotateSouth = 1;
                            renderer.uvRotateNorth = 2;
                            renderer.uvRotateTop = 1;
                            renderer.uvRotateBottom = 2;
                            iconXPos = block.TOP_TIP;
                            iconXNeg = block.BOTTOM_TIP;
                            iconZPos = block.FACE_RIGHT;
                            iconZNeg = block.FACE_RIGHT;
                            break;
                        case NORTH:
                            renderer.uvRotateEast = 1;
                            renderer.uvRotateWest = 2;
                            renderer.uvRotateSouth = 2;
                            renderer.uvRotateNorth = 1;
                            renderer.uvRotateTop = 0;
                            renderer.uvRotateBottom = 3;
                            iconXPos = block.FACE_RIGHT;
                            iconXNeg = block.FACE_RIGHT;
                            iconZPos = block.TOP_TIP;
                            iconZNeg = block.BOTTOM_TIP;
                            break;
                        case SOUTH:
                            renderer.uvRotateEast = 1;
                            renderer.uvRotateWest = 2;
                            renderer.uvRotateSouth = 1;
                            renderer.uvRotateNorth = 2;
                            renderer.uvRotateTop = 3;
                            renderer.uvRotateBottom = 0;
                            iconXPos = block.FACE_LEFT;
                            iconXNeg = block.FACE_LEFT;
                            iconZPos = block.BOTTOM_TIP;
                            iconZNeg = block.TOP_TIP;
                            break;
                    }

                    iconYPos = block.CROSS_SECTION;
                    iconYNeg = block.FACE_FRONT;
                    break;
                case UP:
                    switch (face) {
                        default:
                        case EAST:
                            renderer.uvRotateEast = 1;
                            renderer.uvRotateWest = 2;
                            renderer.uvRotateSouth = 2;
                            renderer.uvRotateNorth = 1;
                            renderer.uvRotateTop = 2;
                            renderer.uvRotateBottom = 1;
                            iconXPos = block.BOTTOM_TIP;
                            iconXNeg = block.TOP_TIP;
                            iconZPos = block.FACE_RIGHT;
                            iconZNeg = block.FACE_RIGHT;
                            break;
                        case WEST:
                            renderer.uvRotateEast = 2;
                            renderer.uvRotateWest = 1;
                            renderer.uvRotateSouth = 2;
                            renderer.uvRotateNorth = 1;
                            renderer.uvRotateTop = 1;
                            renderer.uvRotateBottom = 2;
                            iconXPos = block.TOP_TIP;
                            iconXNeg = block.BOTTOM_TIP;
                            iconZPos = block.FACE_LEFT;
                            iconZNeg = block.FACE_LEFT;
                            break;
                        case NORTH:
                            renderer.uvRotateEast = 2;
                            renderer.uvRotateWest = 1;
                            renderer.uvRotateSouth = 2;
                            renderer.uvRotateNorth = 1;
                            renderer.uvRotateTop = 3;
                            renderer.uvRotateBottom = 0;
                            iconXPos = block.FACE_LEFT;
                            iconXNeg = block.FACE_LEFT;
                            iconZPos = block.TOP_TIP;
                            iconZNeg = block.BOTTOM_TIP;
                            break;
                        case SOUTH:
                            renderer.uvRotateEast = 2;
                            renderer.uvRotateWest = 1;
                            renderer.uvRotateSouth = 1;
                            renderer.uvRotateNorth = 2;
                            renderer.uvRotateTop = 0;
                            renderer.uvRotateBottom = 3;
                            iconXPos = block.FACE_RIGHT;
                            iconXNeg = block.FACE_RIGHT;
                            iconZPos = block.BOTTOM_TIP;
                            iconZNeg = block.TOP_TIP;
                            break;
                    }

                    iconYPos = block.FACE_FRONT;
                    iconYNeg = block.CROSS_SECTION;
                    break;
                default:
                case SIDE:
                    switch (face) {
                        default:
                        case EAST:
                            renderer.uvRotateEast = 0;
                            renderer.uvRotateWest = 0;
                            renderer.uvRotateSouth = 0;
                            renderer.uvRotateNorth = 0;
                            renderer.uvRotateTop = 3;
                            renderer.uvRotateBottom = 3;
                            iconXPos = block.FACE_FRONT;
                            iconXNeg = block.CROSS_SECTION;
                            iconZPos = block.FACE_RIGHT;
                            iconZNeg = block.FACE_LEFT;
                            iconYPos = block.TOP_TIP;
                            iconYNeg = block.BOTTOM_TIP;
                            break;
                        case WEST:
                            renderer.uvRotateEast = 0;
                            renderer.uvRotateWest = 0;
                            renderer.uvRotateSouth = 0;
                            renderer.uvRotateNorth = 0;
                            renderer.uvRotateTop = 0;
                            renderer.uvRotateBottom = 0;
                            iconXPos = block.CROSS_SECTION;
                            iconXNeg = block.FACE_FRONT;
                            iconZPos = block.FACE_LEFT;
                            iconZNeg = block.FACE_RIGHT;
                            iconYPos = block.TOP_TIP;
                            iconYNeg = block.BOTTOM_TIP;
                            break;
                        case NORTH:
                            renderer.uvRotateEast = 0;
                            renderer.uvRotateWest = 0;
                            renderer.uvRotateSouth = 0;
                            renderer.uvRotateNorth = 0;
                            renderer.uvRotateTop = 1;
                            renderer.uvRotateBottom = 2;
                            iconXPos = block.FACE_RIGHT;
                            iconXNeg = block.FACE_LEFT;
                            iconZPos = block.CROSS_SECTION;
                            iconZNeg = block.FACE_FRONT;
                            iconYPos = block.TOP_TIP;
                            iconYNeg = block.BOTTOM_TIP;
                            break;
                        case SOUTH:
                            renderer.uvRotateEast = 0;
                            renderer.uvRotateWest = 0;
                            renderer.uvRotateSouth = 0;
                            renderer.uvRotateNorth = 0;
                            renderer.uvRotateTop = 2;
                            renderer.uvRotateBottom = 1;
                            iconXPos = block.FACE_LEFT;
                            iconXNeg = block.FACE_RIGHT;
                            iconZPos = block.FACE_FRONT;
                            iconZNeg = block.CROSS_SECTION;
                            iconYPos = block.TOP_TIP;
                            iconYNeg = block.BOTTOM_TIP;
                            break;
                    }
                    break;
            }
        }
        // Tails
        else {
            switch (dir) {
                case DOWN:
                    iconYNeg = block.CROSS_SECTION;
                    iconYPos = block.CROSS_SECTION;
                    switch (face) {
                        default:
                        case EAST:
                            // Lower & upper faces
                            switch (te.GetConnectionType(0)) {
                                default:
                                    break;
                                case WS:
                                case WN:
                                case triE:
                                case W:
                                    if (!te.neighbourDown && te.neighbourUp) {
                                        renderer.uvRotateBottom = 0;
                                        iconYNeg = block.BOTTOM_LONG;
                                    }
                                    if (te.neighbourDown && !te.neighbourUp) {
                                        renderer.uvRotateTop = 3;
                                        iconYPos = block.TOP_TIP;
                                    }
                                    break;
                                case ES:
                                case EN:
                                case triW:
                                case E:
                                    if (te.neighbourDown && !te.neighbourUp) {
                                        renderer.uvRotateTop = 0;
                                        iconYPos = block.TOP_LONG;
                                    }
                                    if (!te.neighbourDown && te.neighbourUp) {
                                        renderer.uvRotateBottom = 0;
                                        iconYNeg = block.BOTTOM_TIP;
                                    }
                                    break;
                                case EW:
                                    if (!te.neighbourDown && te.neighbourUp) {
                                        iconYNeg = block.BOTTOM_LONG;
                                    }
                                    if (te.neighbourDown && !te.neighbourUp) {
                                        iconYPos = block.TOP_LONG;
                                    }
                                    break;
                                case triN:
                                    if (!te.neighbourDown && te.neighbourUp) {
                                        renderer.uvRotateBottom = 3;
                                        iconYNeg = block.BOTTOM_TRIPLE_UP;
                                    }
                                    if (te.neighbourDown && !te.neighbourUp) {
                                        renderer.uvRotateTop = 3;
                                        iconYPos = block.TOP_TRIPLE_UP;
                                    }
                                    break;
                                case triS:
                                    if (!te.neighbourDown && te.neighbourUp) {
                                        renderer.uvRotateBottom = 0;
                                        iconYNeg = block.BOTTOM_TRIPLE_UP;
                                    }
                                    if (te.neighbourDown && !te.neighbourUp) {
                                        renderer.uvRotateTop = 0;
                                        iconYPos = block.TOP_TRIPLE_UP;
                                    }
                                    break;
                                case CROSS:
                                    if (!te.neighbourDown && te.neighbourUp) {
                                        iconYNeg = block.BOTTOM_CROSS;
                                    }
                                    if (te.neighbourDown && !te.neighbourUp) {
                                        iconYPos = block.TOP_CROSS;
                                    }
                                    break;
                            }

                            // Eastern & western faces
                            if (te.neighbourEast) iconXPos = block.CROSS_SECTION;
                            else {
                                if (!te.neighbourUp && te.neighbourDown && te.neighbourWest) {
                                    renderer.uvRotateSouth = 2;
                                    iconXPos = block.BOTTOM_TIP;
                                } else {
                                    renderer.uvRotateSouth = 1;
                                    iconXPos = block.BOTTOM_LONG;
                                }
                            }
                            if (te.neighbourWest) iconXNeg = block.CROSS_SECTION;
                            else {
                                if (!te.neighbourDown && te.neighbourUp && te.neighbourEast) {
                                    renderer.uvRotateNorth = 2;
                                    iconXNeg = block.TOP_TIP;
                                } else {
                                    renderer.uvRotateNorth = 1;
                                    iconXNeg = block.TOP_LONG;
                                }
                            }

                            // Northern & southern faces

                            switch (te.GetConnectionType(4)) {
                                case W:
                                case triE:
                                    if (!te.neighbourSouth) {
                                        renderer.uvRotateWest = 2;
                                        iconZPos = block.TRIPLE_RIGHT_UP_RB;
                                    }
                                    if (!te.neighbourNorth) {
                                        renderer.uvRotateEast = 1;
                                        iconZNeg = block.TRIPLE_RIGHT_UP_RB;
                                    }
                                    break;
                                case E:
                                case triW:
                                    if (!te.neighbourSouth) {
                                        renderer.uvRotateWest = 2;
                                        iconZPos = block.TRIPLE_RIGHT_DOWN_RB;
                                    }
                                    if (!te.neighbourNorth) {
                                        renderer.uvRotateEast = 1;
                                        iconZNeg = block.TRIPLE_RIGHT_DOWN_RB;
                                    }
                                    break;
                                default:
                                case D:
                                case U:
                                case UD:
                                case NO:
                                    if (!te.neighbourSouth) {
                                        renderer.uvRotateWest = 2;
                                        iconZPos = block.RIGHT_SIDE;
                                    }
                                    if (!te.neighbourNorth) {
                                        renderer.uvRotateEast = 1;
                                        iconZNeg = block.RIGHT_SIDE;
                                    }
                                    break;
                                case UW:
                                    if (!te.neighbourSouth) {
                                        renderer.uvRotateWest = 0;
                                        iconZPos = block.LEFT_UP_CLEAR;
                                    }
                                    if (!te.neighbourNorth) {
                                        renderer.uvRotateEast = 0;
                                        iconZNeg = block.RIGHT_UP_CLEAR;
                                    }
                                    break;
                                case UE:
                                    if (!te.neighbourSouth) {
                                        renderer.uvRotateWest = 0;
                                        iconZPos = block.RIGHT_UP_ORNATED;
                                    }
                                    if (!te.neighbourNorth) {
                                        renderer.uvRotateEast = 0;
                                        iconZNeg = block.LEFT_UP_ORNATED;
                                    }
                                    break;
                                case DW:
                                    if (!te.neighbourSouth) {
                                        renderer.uvRotateWest = 0;
                                        iconZPos = block.LEFT_DOWN_CLEAR;
                                    }
                                    if (!te.neighbourNorth) {
                                        renderer.uvRotateEast = 0;
                                        iconZNeg = block.RIGHT_DOWN_CLEAR;
                                    }
                                    break;
                                case DE:
                                    if (!te.neighbourSouth) {
                                        renderer.uvRotateWest = 0;
                                        iconZPos = block.RIGHT_DOWN_ORNATED;
                                    }
                                    if (!te.neighbourNorth) {
                                        renderer.uvRotateEast = 0;
                                        iconZNeg = block.LEFT_DOWN_ORNATED;
                                    }
                                    break;
                                case EW:
                                case CROSS:
                                    if (!te.neighbourSouth) {
                                        renderer.uvRotateWest = 2;
                                        iconZPos = block.LEFT_CROSS;
                                    }
                                    if (!te.neighbourNorth) {
                                        renderer.uvRotateEast = 1;
                                        iconZNeg = block.LEFT_CROSS;
                                    }
                                    break;
                                case triU:
                                    if (!te.neighbourSouth) {
                                        renderer.uvRotateWest = 0;
                                        iconZPos = block.TRIPLE_RIGHT_DOWN_LB;
                                    }
                                    if (!te.neighbourNorth) {
                                        renderer.uvRotateEast = 0;
                                        iconZNeg = block.TRIPLE_LEFT_DOWN_RB;
                                    }
                                    break;
                                case triD:
                                    if (!te.neighbourSouth) {
                                        renderer.uvRotateWest = 0;
                                        iconZPos = block.TRIPLE_RIGHT_UP_LB;
                                    }
                                    if (!te.neighbourNorth) {
                                        renderer.uvRotateEast = 0;
                                        iconZNeg = block.TRIPLE_LEFT_UP_RB;
                                    }
                                    break;
                            }

                            break;
                        case WEST:
                            // Lower & upper faces
                            switch (te.GetConnectionType(0)) {
                                default:
                                    break;
                                case WS:
                                case WN:
                                case triE:
                                case W:
                                    if (te.neighbourDown && !te.neighbourUp) {
                                        renderer.uvRotateTop = 3;
                                        iconYPos = block.TOP_LONG;
                                    }
                                    if (!te.neighbourDown && te.neighbourUp) {
                                        renderer.uvRotateBottom = 3;
                                        iconYNeg = block.BOTTOM_TIP;
                                    }
                                    break;
                                case ES:
                                case EN:
                                case triW:
                                case E:
                                    if (!te.neighbourDown && te.neighbourUp) {
                                        renderer.uvRotateBottom = 3;
                                        iconYNeg = block.BOTTOM_LONG;
                                    }
                                    if (te.neighbourDown && !te.neighbourUp) {
                                        renderer.uvRotateTop = 0;
                                        iconYPos = block.TOP_TIP;
                                    }
                                    break;
                                case EW:
                                    if (!te.neighbourDown && te.neighbourUp) {
                                        iconYNeg = block.BOTTOM_LONG;
                                    }
                                    if (te.neighbourDown && !te.neighbourUp) {
                                        iconYPos = block.TOP_LONG;
                                    }
                                    break;
                                case triN:
                                    if (!te.neighbourDown && te.neighbourUp) {
                                        renderer.uvRotateBottom = 3;
                                        iconYNeg = block.BOTTOM_TRIPLE_UP;
                                    }
                                    if (te.neighbourDown && !te.neighbourUp) {
                                        renderer.uvRotateTop = 3;
                                        iconYPos = block.TOP_TRIPLE_UP;
                                    }
                                    break;
                                case triS:
                                    if (!te.neighbourDown && te.neighbourUp) {
                                        renderer.uvRotateBottom = 0;
                                        iconYNeg = block.BOTTOM_TRIPLE_UP;
                                    }
                                    if (te.neighbourDown && !te.neighbourUp) {
                                        renderer.uvRotateTop = 0;
                                        iconYPos = block.TOP_TRIPLE_UP;
                                    }
                                    break;
                                case CROSS:
                                    if (!te.neighbourDown && te.neighbourUp) {
                                        iconYNeg = block.BOTTOM_CROSS;
                                    }
                                    if (te.neighbourDown && !te.neighbourUp) {
                                        iconYPos = block.TOP_CROSS;
                                    }
                                    break;
                            }

                            // Eastern & western faces
                            if (te.neighbourEast) iconXPos = block.CROSS_SECTION;
                            else {
                                if (!te.neighbourDown && te.neighbourUp && te.neighbourWest) {
                                    renderer.uvRotateSouth = 1;
                                    iconXPos = block.TOP_TIP;
                                } else {
                                    renderer.uvRotateSouth = 2;
                                    iconXPos = block.TOP_LONG;
                                }
                            }
                            if (te.neighbourWest) iconXNeg = block.CROSS_SECTION;
                            else {
                                if (!te.neighbourUp && te.neighbourDown && te.neighbourEast) {
                                    renderer.uvRotateNorth = 1;
                                    iconXNeg = block.BOTTOM_TIP;
                                } else {
                                    renderer.uvRotateNorth = 2;
                                    iconXNeg = block.BOTTOM_LONG;
                                }
                            }

                            // Northern & southern faces

                            switch (te.GetConnectionType(4)) {
                                case W:
                                case triE:
                                    if (!te.neighbourSouth) {
                                        renderer.uvRotateWest = 1;
                                        iconZPos = block.TRIPLE_LEFT_DOWN_LB;
                                    }
                                    if (!te.neighbourNorth) {
                                        renderer.uvRotateEast = 2;
                                        iconZNeg = block.TRIPLE_LEFT_DOWN_LB;
                                    }
                                    break;
                                case E:
                                case triW:
                                    if (!te.neighbourSouth) {
                                        renderer.uvRotateWest = 1;
                                        iconZPos = block.TRIPLE_LEFT_UP_LB;
                                    }
                                    if (!te.neighbourNorth) {
                                        renderer.uvRotateEast = 2;
                                        iconZNeg = block.TRIPLE_LEFT_UP_LB;
                                    }
                                    break;
                                default:
                                case D:
                                case U:
                                case UD:
                                case NO:
                                    if (!te.neighbourSouth) {
                                        renderer.uvRotateWest = 1;
                                        iconZPos = block.LEFT_SIDE;
                                    }
                                    if (!te.neighbourNorth) {
                                        renderer.uvRotateEast = 2;
                                        iconZNeg = block.LEFT_SIDE;
                                    }
                                    break;
                                case UW:
                                    if (!te.neighbourSouth) {
                                        renderer.uvRotateWest = 0;
                                        iconZPos = block.LEFT_UP_ORNATED;
                                    }
                                    if (!te.neighbourNorth) {
                                        renderer.uvRotateEast = 0;
                                        iconZNeg = block.RIGHT_UP_ORNATED;
                                    }
                                    break;
                                case UE:
                                    if (!te.neighbourSouth) {
                                        renderer.uvRotateWest = 0;
                                        iconZPos = block.RIGHT_UP_CLEAR;
                                    }
                                    if (!te.neighbourNorth) {
                                        renderer.uvRotateEast = 0;
                                        iconZNeg = block.LEFT_UP_CLEAR;
                                    }
                                    break;
                                case DW:
                                    if (!te.neighbourSouth) {
                                        renderer.uvRotateWest = 0;
                                        iconZPos = block.LEFT_DOWN_ORNATED;
                                    }
                                    if (!te.neighbourNorth) {
                                        renderer.uvRotateEast = 0;
                                        iconZNeg = block.RIGHT_DOWN_ORNATED;
                                    }
                                    break;
                                case DE:
                                    if (!te.neighbourSouth) {
                                        renderer.uvRotateWest = 0;
                                        iconZPos = block.RIGHT_DOWN_CLEAR;
                                    }
                                    if (!te.neighbourNorth) {
                                        renderer.uvRotateEast = 0;
                                        iconZNeg = block.LEFT_DOWN_CLEAR;
                                    }
                                    break;
                                case EW:
                                case CROSS:
                                    if (!te.neighbourSouth) {
                                        renderer.uvRotateWest = 0;
                                        iconZPos = block.LEFT_CROSS;
                                    }
                                    if (!te.neighbourNorth) {
                                        renderer.uvRotateEast = 0;
                                        iconZNeg = block.RIGHT_CROSS;
                                    }
                                    break;
                                case triU:
                                    if (!te.neighbourSouth) {
                                        renderer.uvRotateWest = 0;
                                        iconZPos = block.TRIPLE_LEFT_DOWN_RB;
                                    }
                                    if (!te.neighbourNorth) {
                                        renderer.uvRotateEast = 0;
                                        iconZNeg = block.TRIPLE_RIGHT_DOWN_LB;
                                    }
                                    break;
                                case triD:
                                    if (!te.neighbourSouth) {
                                        renderer.uvRotateWest = 0;
                                        iconZPos = block.TRIPLE_LEFT_UP_RB;
                                    }
                                    if (!te.neighbourNorth) {
                                        renderer.uvRotateEast = 0;
                                        iconZNeg = block.TRIPLE_RIGHT_UP_LB;
                                    }
                                    break;
                            }

                            break;
                        case NORTH:
                            // Lower & upper faces
                            switch (te.GetConnectionType(0)) {
                                default:
                                    break;
                                case WN:
                                case EN:
                                case triS:
                                case N:
                                    if (te.neighbourDown && !te.neighbourUp) {
                                        renderer.uvRotateTop = 1;
                                        iconYPos = block.TOP_LONG;
                                    }
                                    if (!te.neighbourDown && te.neighbourUp) {
                                        renderer.uvRotateBottom = 1;
                                        iconYNeg = block.BOTTOM_TIP;
                                    }
                                    break;
                                case WS:
                                case ES:
                                case triN:
                                case S:
                                    if (!te.neighbourDown && te.neighbourUp) {
                                        renderer.uvRotateBottom = 1;
                                        iconYNeg = block.BOTTOM_LONG;
                                    }
                                    if (te.neighbourDown && !te.neighbourUp) {
                                        renderer.uvRotateTop = 1;
                                        iconYPos = block.TOP_TIP;
                                    }
                                    break;
                                case NS:
                                    if (!te.neighbourDown && te.neighbourUp) {
                                        renderer.uvRotateBottom = 2;
                                        iconYNeg = block.BOTTOM_LONG;
                                    }
                                    if (te.neighbourDown && !te.neighbourUp) {
                                        renderer.uvRotateTop = 2;
                                        iconYPos = block.TOP_LONG;
                                    }
                                    break;
                                case triE:
                                    if (!te.neighbourDown && te.neighbourUp) {
                                        renderer.uvRotateBottom = 1;
                                        iconYNeg = block.BOTTOM_TRIPLE_UP;
                                    }
                                    if (te.neighbourDown && !te.neighbourUp) {
                                        renderer.uvRotateTop = 2;
                                        iconYPos = block.TOP_TRIPLE_UP;
                                    }
                                    break;
                                case triW:
                                    if (!te.neighbourDown && te.neighbourUp) {
                                        renderer.uvRotateBottom = 2;
                                        iconYNeg = block.BOTTOM_TRIPLE_UP;
                                    }
                                    if (te.neighbourDown && !te.neighbourUp) {
                                        renderer.uvRotateTop = 1;
                                        iconYPos = block.TOP_TRIPLE_UP;
                                    }
                                    break;
                                case CROSS:
                                    if (!te.neighbourDown && te.neighbourUp) {
                                        iconYNeg = block.BOTTOM_CROSS;
                                    }
                                    if (te.neighbourDown && !te.neighbourUp) {
                                        iconYPos = block.TOP_CROSS;
                                    }
                                    break;
                            }

                            // Eastern & western faces

                            switch (te.GetConnectionType(2)) {
                                case N:
                                case triS:
                                    if (!te.neighbourEast) {
                                        renderer.uvRotateSouth = 2;
                                        iconXPos = block.TRIPLE_LEFT_DOWN_LB;
                                    }
                                    if (!te.neighbourWest) {
                                        renderer.uvRotateNorth = 1;
                                        iconXNeg = block.TRIPLE_LEFT_DOWN_LB;
                                    }
                                    break;
                                case S:
                                case triN:
                                    if (!te.neighbourEast) {
                                        renderer.uvRotateSouth = 2;
                                        iconXPos = block.TRIPLE_LEFT_UP_LB;
                                    }
                                    if (!te.neighbourWest) {
                                        renderer.uvRotateNorth = 1;
                                        iconXNeg = block.TRIPLE_LEFT_UP_LB;
                                    }
                                    break;
                                default:
                                case D:
                                case U:
                                case UD:
                                case NO:
                                    if (!te.neighbourEast) {
                                        renderer.uvRotateSouth = 2;
                                        iconXPos = block.LEFT_SIDE;
                                    }
                                    if (!te.neighbourWest) {
                                        renderer.uvRotateNorth = 1;
                                        iconXNeg = block.LEFT_SIDE;
                                    }
                                    break;
                                case UN:
                                    if (!te.neighbourEast) {
                                        renderer.uvRotateSouth = 0;
                                        iconXPos = block.RIGHT_UP_ORNATED;
                                    }
                                    if (!te.neighbourWest) {
                                        renderer.uvRotateNorth = 0;
                                        iconXNeg = block.LEFT_UP_ORNATED;
                                    }
                                    break;
                                case US:
                                    if (!te.neighbourEast) {
                                        renderer.uvRotateSouth = 0;
                                        iconXPos = block.LEFT_UP_CLEAR;
                                    }
                                    if (!te.neighbourWest) {
                                        renderer.uvRotateNorth = 0;
                                        iconXNeg = block.RIGHT_UP_CLEAR;
                                    }
                                    break;
                                case DN:
                                    if (!te.neighbourEast) {
                                        renderer.uvRotateSouth = 0;
                                        iconXPos = block.RIGHT_DOWN_ORNATED;
                                    }
                                    if (!te.neighbourWest) {
                                        renderer.uvRotateNorth = 0;
                                        iconXNeg = block.LEFT_DOWN_ORNATED;
                                    }
                                    break;
                                case DS:
                                    if (!te.neighbourEast) {
                                        renderer.uvRotateSouth = 0;
                                        iconXPos = block.LEFT_DOWN_CLEAR;
                                    }
                                    if (!te.neighbourWest) {
                                        renderer.uvRotateNorth = 0;
                                        iconXNeg = block.RIGHT_DOWN_CLEAR;
                                    }
                                    break;
                                case NS:
                                case CROSS:
                                    if (!te.neighbourEast) {
                                        renderer.uvRotateSouth = 0;
                                        iconXPos = block.RIGHT_CROSS;
                                    }
                                    if (!te.neighbourWest) {
                                        renderer.uvRotateNorth = 0;
                                        iconXNeg = block.LEFT_CROSS;
                                    }
                                    break;
                                case triU:
                                    if (!te.neighbourEast) {
                                        renderer.uvRotateSouth = 0;
                                        iconXPos = block.TRIPLE_RIGHT_DOWN_LB;
                                    }
                                    if (!te.neighbourWest) {
                                        renderer.uvRotateNorth = 0;
                                        iconXNeg = block.TRIPLE_LEFT_DOWN_RB;
                                    }
                                    break;
                                case triD:
                                    if (!te.neighbourEast) {
                                        renderer.uvRotateSouth = 0;
                                        iconXPos = block.TRIPLE_RIGHT_UP_LB;
                                    }
                                    if (!te.neighbourWest) {
                                        renderer.uvRotateNorth = 0;
                                        iconXNeg = block.TRIPLE_LEFT_UP_RB;
                                    }
                                    break;
                            }

                            // Northern & southern faces
                            if (te.neighbourSouth) iconZPos = block.CROSS_SECTION;
                            else {
                                if (!te.neighbourDown && te.neighbourUp && te.neighbourNorth) {
                                    renderer.uvRotateWest = 2;
                                    iconZPos = block.TOP_TIP;
                                } else {
                                    renderer.uvRotateWest = 2;
                                    iconZPos = block.TOP_LONG;
                                }
                            }
                            if (te.neighbourNorth) iconZNeg = block.CROSS_SECTION;
                            else {
                                if (!te.neighbourUp && te.neighbourDown && te.neighbourSouth) {
                                    renderer.uvRotateEast = 2;
                                    iconZNeg = block.BOTTOM_TIP;
                                } else {
                                    renderer.uvRotateEast = 1;
                                    iconZNeg = block.BOTTOM_LONG;
                                }
                            }

                            break;
                        case SOUTH:
                            // Lower & upper faces
                            switch (te.GetConnectionType(0)) {
                                default:
                                    break;
                                case WN:
                                case EN:
                                case triS:
                                case N:
                                    if (!te.neighbourDown && te.neighbourUp) {
                                        renderer.uvRotateBottom = 2;
                                        iconYNeg = block.BOTTOM_LONG;
                                    }
                                    if (te.neighbourDown && !te.neighbourUp) {
                                        renderer.uvRotateTop = 2;
                                        iconYPos = block.TOP_TIP;
                                    }
                                    break;
                                case WS:
                                case ES:
                                case triN:
                                case S:
                                    if (te.neighbourDown && !te.neighbourUp) {
                                        renderer.uvRotateTop = 2;
                                        iconYPos = block.TOP_LONG;
                                    }
                                    if (!te.neighbourDown && te.neighbourUp) {
                                        renderer.uvRotateBottom = 2;
                                        iconYNeg = block.BOTTOM_TIP;
                                    }
                                    break;
                                case NS:
                                    if (!te.neighbourDown && te.neighbourUp) {
                                        renderer.uvRotateBottom = 2;
                                        iconYNeg = block.BOTTOM_LONG;
                                    }
                                    if (te.neighbourDown && !te.neighbourUp) {
                                        renderer.uvRotateTop = 2;
                                        iconYPos = block.TOP_LONG;
                                    }
                                    break;
                                case triE:
                                    if (!te.neighbourDown && te.neighbourUp) {
                                        renderer.uvRotateBottom = 1;
                                        iconYNeg = block.BOTTOM_TRIPLE_UP;
                                    }
                                    if (te.neighbourDown && !te.neighbourUp) {
                                        renderer.uvRotateTop = 2;
                                        iconYPos = block.TOP_TRIPLE_UP;
                                    }
                                    break;
                                case triW:
                                    if (!te.neighbourDown && te.neighbourUp) {
                                        renderer.uvRotateBottom = 2;
                                        iconYNeg = block.BOTTOM_TRIPLE_UP;
                                    }
                                    if (te.neighbourDown && !te.neighbourUp) {
                                        renderer.uvRotateTop = 1;
                                        iconYPos = block.TOP_TRIPLE_UP;
                                    }
                                    break;
                                case CROSS:
                                    if (!te.neighbourDown && te.neighbourUp) {
                                        iconYNeg = block.BOTTOM_CROSS;
                                    }
                                    if (te.neighbourDown && !te.neighbourUp) {
                                        iconYPos = block.TOP_CROSS;
                                    }
                                    break;
                            }

                            // Eastern & western faces

                            switch (te.GetConnectionType(2)) {
                                case N:
                                case triS:
                                    if (!te.neighbourEast) {
                                        renderer.uvRotateSouth = 1;
                                        iconXPos = block.TRIPLE_RIGHT_UP_RB;
                                    }
                                    if (!te.neighbourWest) {
                                        renderer.uvRotateNorth = 2;
                                        iconXNeg = block.TRIPLE_RIGHT_UP_RB;
                                    }
                                    break;
                                case S:
                                case triN:
                                    if (!te.neighbourEast) {
                                        renderer.uvRotateSouth = 1;
                                        iconXPos = block.TRIPLE_RIGHT_DOWN_RB;
                                    }
                                    if (!te.neighbourWest) {
                                        renderer.uvRotateNorth = 2;
                                        iconXNeg = block.TRIPLE_RIGHT_DOWN_RB;
                                    }
                                    break;
                                default:
                                case D:
                                case U:
                                case UD:
                                case NO:
                                    if (!te.neighbourEast) {
                                        renderer.uvRotateSouth = 1;
                                        iconXPos = block.RIGHT_SIDE;
                                    }
                                    if (!te.neighbourWest) {
                                        renderer.uvRotateNorth = 2;
                                        iconXNeg = block.RIGHT_SIDE;
                                    }
                                    break;
                                case UN:
                                    if (!te.neighbourEast) {
                                        renderer.uvRotateSouth = 0;
                                        iconXPos = block.RIGHT_UP_CLEAR;
                                    }
                                    if (!te.neighbourWest) {
                                        renderer.uvRotateNorth = 0;
                                        iconXNeg = block.LEFT_UP_CLEAR;
                                    }
                                    break;
                                case US:
                                    if (!te.neighbourEast) {
                                        renderer.uvRotateSouth = 0;
                                        iconXPos = block.LEFT_UP_ORNATED;
                                    }
                                    if (!te.neighbourWest) {
                                        renderer.uvRotateNorth = 0;
                                        iconXNeg = block.RIGHT_UP_ORNATED;
                                    }
                                    break;
                                case DN:
                                    if (!te.neighbourEast) {
                                        renderer.uvRotateSouth = 0;
                                        iconXPos = block.RIGHT_DOWN_CLEAR;
                                    }
                                    if (!te.neighbourWest) {
                                        renderer.uvRotateNorth = 0;
                                        iconXNeg = block.LEFT_DOWN_CLEAR;
                                    }
                                    break;
                                case DS:
                                    if (!te.neighbourEast) {
                                        renderer.uvRotateSouth = 0;
                                        iconXPos = block.LEFT_DOWN_ORNATED;
                                    }
                                    if (!te.neighbourWest) {
                                        renderer.uvRotateNorth = 0;
                                        iconXNeg = block.RIGHT_DOWN_ORNATED;
                                    }
                                    break;
                                case NS:
                                case CROSS:
                                    if (!te.neighbourEast) {
                                        renderer.uvRotateSouth = 0;
                                        iconXPos = block.LEFT_CROSS;
                                    }
                                    if (!te.neighbourWest) {
                                        renderer.uvRotateNorth = 0;
                                        iconXNeg = block.RIGHT_CROSS;
                                    }
                                    break;
                                case triU:
                                    if (!te.neighbourEast) {
                                        renderer.uvRotateSouth = 0;
                                        iconXPos = block.TRIPLE_LEFT_DOWN_RB;
                                    }
                                    if (!te.neighbourWest) {
                                        renderer.uvRotateNorth = 0;
                                        iconXNeg = block.TRIPLE_RIGHT_DOWN_LB;
                                    }
                                    break;
                                case triD:
                                    if (!te.neighbourEast) {
                                        renderer.uvRotateSouth = 0;
                                        iconXPos = block.TRIPLE_LEFT_UP_RB;
                                    }
                                    if (!te.neighbourWest) {
                                        renderer.uvRotateNorth = 0;
                                        iconXNeg = block.TRIPLE_RIGHT_UP_LB;
                                    }
                                    break;
                            }

                            // Northern & southern faces
                            if (te.neighbourSouth) iconZPos = block.CROSS_SECTION;
                            else {
                                if (!te.neighbourUp && te.neighbourDown && te.neighbourNorth) {
                                    renderer.uvRotateWest = 1;
                                    iconZPos = block.BOTTOM_TIP;
                                } else {
                                    renderer.uvRotateWest = 2;
                                    iconZPos = block.BOTTOM_LONG;
                                }
                            }
                            if (te.neighbourNorth) iconZNeg = block.CROSS_SECTION;
                            else {
                                if (!te.neighbourDown && te.neighbourUp && te.neighbourSouth) {
                                    renderer.uvRotateEast = 1;
                                    iconZNeg = block.TOP_TIP;
                                } else {
                                    renderer.uvRotateEast = 2;
                                    iconZNeg = block.TOP_LONG;
                                }
                            }

                            break;
                    }
                    break;
                case UP:
                    iconYNeg = block.CROSS_SECTION;
                    iconYPos = block.CROSS_SECTION;
                    switch (face) {
                        default:
                        case EAST:
                            // Lower & upper faces
                            switch (te.GetConnectionType(0)) {
                                default:
                                    break;
                                case WS:
                                case WN:
                                case triE:
                                case W:
                                    if (!te.neighbourDown && te.neighbourUp) {
                                        renderer.uvRotateBottom = 0;
                                        iconYNeg = block.BOTTOM_LONG;
                                    }
                                    if (te.neighbourDown && !te.neighbourUp) {
                                        renderer.uvRotateTop = 3;
                                        iconYPos = block.TOP_TIP;
                                    }
                                    break;
                                case ES:
                                case EN:
                                case triW:
                                case E:
                                    if (te.neighbourDown && !te.neighbourUp) {
                                        renderer.uvRotateTop = 0;
                                        iconYPos = block.TOP_LONG;
                                    }
                                    if (!te.neighbourDown && te.neighbourUp) {
                                        renderer.uvRotateBottom = 0;
                                        iconYNeg = block.BOTTOM_TIP;
                                    }
                                    break;
                                case EW:
                                    if (!te.neighbourDown && te.neighbourUp) {
                                        iconYNeg = block.BOTTOM_LONG;
                                    }
                                    if (te.neighbourDown && !te.neighbourUp) {
                                        iconYPos = block.TOP_LONG;
                                    }
                                    break;
                                case triN:
                                    if (!te.neighbourDown && te.neighbourUp) {
                                        renderer.uvRotateBottom = 3;
                                        iconYNeg = block.BOTTOM_TRIPLE_UP;
                                    }
                                    if (te.neighbourDown && !te.neighbourUp) {
                                        renderer.uvRotateTop = 3;
                                        iconYPos = block.TOP_TRIPLE_UP;
                                    }
                                    break;
                                case triS:
                                    if (!te.neighbourDown && te.neighbourUp) {
                                        renderer.uvRotateBottom = 0;
                                        iconYNeg = block.BOTTOM_TRIPLE_UP;
                                    }
                                    if (te.neighbourDown && !te.neighbourUp) {
                                        renderer.uvRotateTop = 0;
                                        iconYPos = block.TOP_TRIPLE_UP;
                                    }
                                    break;
                                case CROSS:
                                    if (!te.neighbourDown && te.neighbourUp) {
                                        iconYNeg = block.BOTTOM_CROSS;
                                    }
                                    if (te.neighbourDown && !te.neighbourUp) {
                                        iconYPos = block.TOP_CROSS;
                                    }
                                    break;
                            }

                            // Eastern & western faces
                            if (te.neighbourEast) iconXPos = block.CROSS_SECTION;
                            else {
                                if (!te.neighbourUp && te.neighbourDown && te.neighbourWest) {
                                    renderer.uvRotateSouth = 2;
                                    iconXPos = block.BOTTOM_TIP;
                                } else {
                                    renderer.uvRotateSouth = 2;
                                    iconXPos = block.BOTTOM_LONG;
                                }
                            }
                            if (te.neighbourWest) iconXNeg = block.CROSS_SECTION;
                            else {
                                if (!te.neighbourDown && te.neighbourUp && te.neighbourEast) {
                                    renderer.uvRotateNorth = 2;
                                    iconXNeg = block.TOP_TIP;
                                } else {
                                    renderer.uvRotateNorth = 1;
                                    iconXNeg = block.TOP_LONG;
                                }
                            }

                            // Northern & southern faces

                            switch (te.GetConnectionType(4)) {
                                case W:
                                case triE:
                                    if (!te.neighbourSouth) {
                                        renderer.uvRotateWest = 2;
                                        iconZPos = block.TRIPLE_LEFT_UP_RB;
                                    }
                                    if (!te.neighbourNorth) {
                                        renderer.uvRotateEast = 1;
                                        iconZNeg = block.TRIPLE_LEFT_UP_RB;
                                    }
                                    break;
                                case E:
                                case triW:
                                    if (!te.neighbourSouth) {
                                        renderer.uvRotateWest = 2;
                                        iconZPos = block.TRIPLE_LEFT_DOWN_RB;
                                    }
                                    if (!te.neighbourNorth) {
                                        renderer.uvRotateEast = 1;
                                        iconZNeg = block.TRIPLE_LEFT_DOWN_RB;
                                    }
                                    break;
                                default:
                                case D:
                                case U:
                                case UD:
                                case NO:
                                    if (!te.neighbourSouth) {
                                        renderer.uvRotateWest = 2;
                                        iconZPos = block.LEFT_SIDE;
                                    }
                                    if (!te.neighbourNorth) {
                                        renderer.uvRotateEast = 1;
                                        iconZNeg = block.LEFT_SIDE;
                                    }
                                    break;
                                case UW:
                                    if (!te.neighbourSouth) {
                                        renderer.uvRotateWest = 0;
                                        iconZPos = block.LEFT_UP_CLEAR;
                                    }
                                    if (!te.neighbourNorth) {
                                        renderer.uvRotateEast = 0;
                                        iconZNeg = block.RIGHT_UP_CLEAR;
                                    }
                                    break;
                                case UE:
                                    if (!te.neighbourSouth) {
                                        renderer.uvRotateWest = 0;
                                        iconZPos = block.RIGHT_UP_ORNATED;
                                    }
                                    if (!te.neighbourNorth) {
                                        renderer.uvRotateEast = 0;
                                        iconZNeg = block.LEFT_UP_ORNATED;
                                    }
                                    break;
                                case DW:
                                    if (!te.neighbourSouth) {
                                        renderer.uvRotateWest = 0;
                                        iconZPos = block.LEFT_DOWN_CLEAR;
                                    }
                                    if (!te.neighbourNorth) {
                                        renderer.uvRotateEast = 0;
                                        iconZNeg = block.RIGHT_DOWN_CLEAR;
                                    }
                                    break;
                                case DE:
                                    if (!te.neighbourSouth) {
                                        renderer.uvRotateWest = 0;
                                        iconZPos = block.RIGHT_DOWN_ORNATED;
                                    }
                                    if (!te.neighbourNorth) {
                                        renderer.uvRotateEast = 0;
                                        iconZNeg = block.LEFT_DOWN_ORNATED;
                                    }
                                    break;
                                case EW:
                                case CROSS:
                                    if (!te.neighbourSouth) {
                                        renderer.uvRotateWest = 2;
                                        iconZPos = block.LEFT_CROSS;
                                    }
                                    if (!te.neighbourNorth) {
                                        renderer.uvRotateEast = 1;
                                        iconZNeg = block.LEFT_CROSS;
                                    }
                                    break;
                                case triU:
                                    if (!te.neighbourSouth) {
                                        renderer.uvRotateWest = 0;
                                        iconZPos = block.TRIPLE_RIGHT_DOWN_LB;
                                    }
                                    if (!te.neighbourNorth) {
                                        renderer.uvRotateEast = 0;
                                        iconZNeg = block.TRIPLE_LEFT_DOWN_RB;
                                    }
                                    break;
                                case triD:
                                    if (!te.neighbourSouth) {
                                        renderer.uvRotateWest = 0;
                                        iconZPos = block.TRIPLE_RIGHT_UP_LB;
                                    }
                                    if (!te.neighbourNorth) {
                                        renderer.uvRotateEast = 0;
                                        iconZNeg = block.TRIPLE_LEFT_UP_RB;
                                    }
                                    break;
                            }

                            break;
                        case WEST:
                            // Lower & upper faces
                            switch (te.GetConnectionType(0)) {
                                default:
                                    break;
                                case WS:
                                case WN:
                                case triE:
                                case W:
                                    if (te.neighbourDown && !te.neighbourUp) {
                                        renderer.uvRotateTop = 3;
                                        iconYPos = block.TOP_LONG;
                                    }
                                    if (!te.neighbourDown && te.neighbourUp) {
                                        renderer.uvRotateBottom = 3;
                                        iconYNeg = block.BOTTOM_TIP;
                                    }
                                    break;
                                case ES:
                                case EN:
                                case triW:
                                case E:
                                    if (!te.neighbourDown && te.neighbourUp) {
                                        renderer.uvRotateBottom = 3;
                                        iconYNeg = block.BOTTOM_LONG;
                                    }
                                    if (te.neighbourDown && !te.neighbourUp) {
                                        renderer.uvRotateTop = 0;
                                        iconYPos = block.TOP_TIP;
                                    }
                                    break;
                                case EW:
                                    if (!te.neighbourDown && te.neighbourUp) {
                                        iconYNeg = block.BOTTOM_LONG;
                                    }
                                    if (te.neighbourDown && !te.neighbourUp) {
                                        iconYPos = block.TOP_LONG;
                                    }
                                    break;
                                case triN:
                                    if (!te.neighbourDown && te.neighbourUp) {
                                        renderer.uvRotateBottom = 3;
                                        iconYNeg = block.BOTTOM_TRIPLE_UP;
                                    }
                                    if (te.neighbourDown && !te.neighbourUp) {
                                        renderer.uvRotateTop = 3;
                                        iconYPos = block.TOP_TRIPLE_UP;
                                    }
                                    break;
                                case triS:
                                    if (!te.neighbourDown && te.neighbourUp) {
                                        renderer.uvRotateBottom = 0;
                                        iconYNeg = block.BOTTOM_TRIPLE_UP;
                                    }
                                    if (te.neighbourDown && !te.neighbourUp) {
                                        renderer.uvRotateTop = 0;
                                        iconYPos = block.TOP_TRIPLE_UP;
                                    }
                                    break;
                                case CROSS:
                                    if (!te.neighbourDown && te.neighbourUp) {
                                        iconYNeg = block.BOTTOM_CROSS;
                                    }
                                    if (te.neighbourDown && !te.neighbourUp) {
                                        iconYPos = block.TOP_CROSS;
                                    }
                                    break;
                            }

                            // Eastern & western faces
                            if (te.neighbourEast) iconXPos = block.CROSS_SECTION;
                            else {
                                if (!te.neighbourDown && te.neighbourUp && te.neighbourWest) {
                                    renderer.uvRotateSouth = 1;
                                    iconXPos = block.TOP_TIP;
                                } else {
                                    renderer.uvRotateSouth = 2;
                                    iconXPos = block.TOP_LONG;
                                }
                            }
                            if (te.neighbourWest) iconXNeg = block.CROSS_SECTION;
                            else {
                                if (!te.neighbourUp && te.neighbourDown && te.neighbourEast) {
                                    renderer.uvRotateNorth = 1;
                                    iconXNeg = block.BOTTOM_TIP;
                                } else {
                                    renderer.uvRotateNorth = 1;
                                    iconXNeg = block.BOTTOM_LONG;
                                }
                            }

                            // Northern & southern faces

                            switch (te.GetConnectionType(4)) {
                                case W:
                                case triE:
                                    if (!te.neighbourSouth) {
                                        renderer.uvRotateWest = 1;
                                        iconZPos = block.TRIPLE_RIGHT_DOWN_LB;
                                    }
                                    if (!te.neighbourNorth) {
                                        renderer.uvRotateEast = 2;
                                        iconZNeg = block.TRIPLE_RIGHT_DOWN_LB;
                                    }
                                    break;
                                case E:
                                case triW:
                                    if (!te.neighbourSouth) {
                                        renderer.uvRotateWest = 1;
                                        iconZPos = block.TRIPLE_RIGHT_UP_LB;
                                    }
                                    if (!te.neighbourNorth) {
                                        renderer.uvRotateEast = 2;
                                        iconZNeg = block.TRIPLE_RIGHT_UP_LB;
                                    }
                                    break;
                                default:
                                case D:
                                case U:
                                case UD:
                                case NO:
                                    if (!te.neighbourSouth) {
                                        renderer.uvRotateWest = 1;
                                        iconZPos = block.RIGHT_SIDE;
                                    }
                                    if (!te.neighbourNorth) {
                                        renderer.uvRotateEast = 2;
                                        iconZNeg = block.RIGHT_SIDE;
                                    }
                                    break;
                                case UW:
                                    if (!te.neighbourSouth) {
                                        renderer.uvRotateWest = 0;
                                        iconZPos = block.LEFT_UP_ORNATED;
                                    }
                                    if (!te.neighbourNorth) {
                                        renderer.uvRotateEast = 0;
                                        iconZNeg = block.RIGHT_UP_ORNATED;
                                    }
                                    break;
                                case UE:
                                    if (!te.neighbourSouth) {
                                        renderer.uvRotateWest = 0;
                                        iconZPos = block.RIGHT_UP_CLEAR;
                                    }
                                    if (!te.neighbourNorth) {
                                        renderer.uvRotateEast = 0;
                                        iconZNeg = block.LEFT_UP_CLEAR;
                                    }
                                    break;
                                case DW:
                                    if (!te.neighbourSouth) {
                                        renderer.uvRotateWest = 0;
                                        iconZPos = block.LEFT_DOWN_ORNATED;
                                    }
                                    if (!te.neighbourNorth) {
                                        renderer.uvRotateEast = 0;
                                        iconZNeg = block.RIGHT_DOWN_ORNATED;
                                    }
                                    break;
                                case DE:
                                    if (!te.neighbourSouth) {
                                        renderer.uvRotateWest = 0;
                                        iconZPos = block.RIGHT_DOWN_CLEAR;
                                    }
                                    if (!te.neighbourNorth) {
                                        renderer.uvRotateEast = 0;
                                        iconZNeg = block.LEFT_DOWN_CLEAR;
                                    }
                                    break;
                                case EW:
                                case CROSS:
                                    if (!te.neighbourSouth) {
                                        renderer.uvRotateWest = 0;
                                        iconZPos = block.LEFT_CROSS;
                                    }
                                    if (!te.neighbourNorth) {
                                        renderer.uvRotateEast = 0;
                                        iconZNeg = block.RIGHT_CROSS;
                                    }
                                    break;
                                case triU:
                                    if (!te.neighbourSouth) {
                                        renderer.uvRotateWest = 0;
                                        iconZPos = block.TRIPLE_LEFT_DOWN_RB;
                                    }
                                    if (!te.neighbourNorth) {
                                        renderer.uvRotateEast = 0;
                                        iconZNeg = block.TRIPLE_RIGHT_DOWN_LB;
                                    }
                                    break;
                                case triD:
                                    if (!te.neighbourSouth) {
                                        renderer.uvRotateWest = 0;
                                        iconZPos = block.TRIPLE_LEFT_UP_RB;
                                    }
                                    if (!te.neighbourNorth) {
                                        renderer.uvRotateEast = 0;
                                        iconZNeg = block.TRIPLE_RIGHT_UP_LB;
                                    }
                                    break;
                            }

                            break;
                        case NORTH:
                            // Lower & upper faces
                            switch (te.GetConnectionType(0)) {
                                default:
                                    break;
                                case WN:
                                case EN:
                                case triS:
                                case N:
                                    if (te.neighbourDown && !te.neighbourUp) {
                                        renderer.uvRotateTop = 1;
                                        iconYPos = block.TOP_LONG;
                                    }
                                    if (!te.neighbourDown && te.neighbourUp) {
                                        renderer.uvRotateBottom = 1;
                                        iconYNeg = block.BOTTOM_TIP;
                                    }
                                    break;
                                case WS:
                                case ES:
                                case triN:
                                case S:
                                    if (!te.neighbourDown && te.neighbourUp) {
                                        renderer.uvRotateBottom = 1;
                                        iconYNeg = block.BOTTOM_LONG;
                                    }
                                    if (te.neighbourDown && !te.neighbourUp) {
                                        renderer.uvRotateTop = 1;
                                        iconYPos = block.TOP_TIP;
                                    }
                                    break;
                                case NS:
                                    if (!te.neighbourDown && te.neighbourUp) {
                                        renderer.uvRotateBottom = 2;
                                        iconYNeg = block.BOTTOM_LONG;
                                    }
                                    if (te.neighbourDown && !te.neighbourUp) {
                                        renderer.uvRotateTop = 2;
                                        iconYPos = block.TOP_LONG;
                                    }
                                    break;
                                case triE:
                                    if (!te.neighbourDown && te.neighbourUp) {
                                        renderer.uvRotateBottom = 1;
                                        iconYNeg = block.BOTTOM_TRIPLE_UP;
                                    }
                                    if (te.neighbourDown && !te.neighbourUp) {
                                        renderer.uvRotateTop = 2;
                                        iconYPos = block.TOP_TRIPLE_UP;
                                    }
                                    break;
                                case triW:
                                    if (!te.neighbourDown && te.neighbourUp) {
                                        renderer.uvRotateBottom = 2;
                                        iconYNeg = block.BOTTOM_TRIPLE_UP;
                                    }
                                    if (te.neighbourDown && !te.neighbourUp) {
                                        renderer.uvRotateTop = 1;
                                        iconYPos = block.TOP_TRIPLE_UP;
                                    }
                                    break;
                                case CROSS:
                                    if (!te.neighbourDown && te.neighbourUp) {
                                        iconYNeg = block.BOTTOM_CROSS;
                                    }
                                    if (te.neighbourDown && !te.neighbourUp) {
                                        iconYPos = block.TOP_CROSS;
                                    }
                                    break;
                            }

                            // Eastern & western faces

                            switch (te.GetConnectionType(2)) {
                                case N:
                                case triS:
                                    if (!te.neighbourEast) {
                                        renderer.uvRotateSouth = 2;
                                        iconXPos = block.TRIPLE_RIGHT_DOWN_LB;
                                    }
                                    if (!te.neighbourWest) {
                                        renderer.uvRotateNorth = 1;
                                        iconXNeg = block.TRIPLE_RIGHT_DOWN_LB;
                                    }
                                    break;
                                case S:
                                case triN:
                                    if (!te.neighbourEast) {
                                        renderer.uvRotateSouth = 2;
                                        iconXPos = block.TRIPLE_RIGHT_UP_LB;
                                    }
                                    if (!te.neighbourWest) {
                                        renderer.uvRotateNorth = 1;
                                        iconXNeg = block.TRIPLE_RIGHT_UP_LB;
                                    }
                                    break;
                                default:
                                case D:
                                case U:
                                case UD:
                                case NO:
                                    if (!te.neighbourEast) {
                                        renderer.uvRotateSouth = 2;
                                        iconXPos = block.RIGHT_SIDE;
                                    }
                                    if (!te.neighbourWest) {
                                        renderer.uvRotateNorth = 1;
                                        iconXNeg = block.RIGHT_SIDE;
                                    }
                                    break;
                                case UN:
                                    if (!te.neighbourEast) {
                                        renderer.uvRotateSouth = 0;
                                        iconXPos = block.RIGHT_UP_ORNATED;
                                    }
                                    if (!te.neighbourWest) {
                                        renderer.uvRotateNorth = 0;
                                        iconXNeg = block.LEFT_UP_ORNATED;
                                    }
                                    break;
                                case US:
                                    if (!te.neighbourEast) {
                                        renderer.uvRotateSouth = 0;
                                        iconXPos = block.LEFT_UP_CLEAR;
                                    }
                                    if (!te.neighbourWest) {
                                        renderer.uvRotateNorth = 0;
                                        iconXNeg = block.RIGHT_UP_CLEAR;
                                    }
                                    break;
                                case DN:
                                    if (!te.neighbourEast) {
                                        renderer.uvRotateSouth = 0;
                                        iconXPos = block.RIGHT_DOWN_ORNATED;
                                    }
                                    if (!te.neighbourWest) {
                                        renderer.uvRotateNorth = 0;
                                        iconXNeg = block.LEFT_DOWN_ORNATED;
                                    }
                                    break;
                                case DS:
                                    if (!te.neighbourEast) {
                                        renderer.uvRotateSouth = 0;
                                        iconXPos = block.LEFT_DOWN_CLEAR;
                                    }
                                    if (!te.neighbourWest) {
                                        renderer.uvRotateNorth = 0;
                                        iconXNeg = block.RIGHT_DOWN_CLEAR;
                                    }
                                    break;
                                case NS:
                                case CROSS:
                                    if (!te.neighbourEast) {
                                        renderer.uvRotateSouth = 0;
                                        iconXPos = block.RIGHT_CROSS;
                                    }
                                    if (!te.neighbourWest) {
                                        renderer.uvRotateNorth = 0;
                                        iconXNeg = block.LEFT_CROSS;
                                    }
                                    break;
                                case triU:
                                    if (!te.neighbourEast) {
                                        renderer.uvRotateSouth = 0;
                                        iconXPos = block.TRIPLE_RIGHT_DOWN_LB;
                                    }
                                    if (!te.neighbourWest) {
                                        renderer.uvRotateNorth = 0;
                                        iconXNeg = block.TRIPLE_LEFT_DOWN_RB;
                                    }
                                    break;
                                case triD:
                                    if (!te.neighbourEast) {
                                        renderer.uvRotateSouth = 0;
                                        iconXPos = block.TRIPLE_RIGHT_UP_LB;
                                    }
                                    if (!te.neighbourWest) {
                                        renderer.uvRotateNorth = 0;
                                        iconXNeg = block.TRIPLE_LEFT_UP_RB;
                                    }
                                    break;
                            }

                            // Northern & southern faces
                            if (te.neighbourSouth) iconZPos = block.CROSS_SECTION;
                            else {
                                if (!te.neighbourDown && te.neighbourUp && te.neighbourNorth) {
                                    renderer.uvRotateWest = 2;
                                    iconZPos = block.TOP_TIP;
                                } else {
                                    renderer.uvRotateWest = 2;
                                    iconZPos = block.TOP_LONG;
                                }
                            }
                            if (te.neighbourNorth) iconZNeg = block.CROSS_SECTION;
                            else {
                                if (!te.neighbourUp && te.neighbourDown && te.neighbourSouth) {
                                    renderer.uvRotateEast = 2;
                                    iconZNeg = block.BOTTOM_TIP;
                                } else {
                                    renderer.uvRotateEast = 2;
                                    iconZNeg = block.BOTTOM_LONG;
                                }
                            }

                            break;
                        case SOUTH:
                            // Lower & upper faces
                            switch (te.GetConnectionType(0)) {
                                default:
                                    break;
                                case WN:
                                case EN:
                                case triS:
                                case N:
                                    if (!te.neighbourDown && te.neighbourUp) {
                                        renderer.uvRotateBottom = 2;
                                        iconYNeg = block.BOTTOM_LONG;
                                    }
                                    if (te.neighbourDown && !te.neighbourUp) {
                                        renderer.uvRotateTop = 2;
                                        iconYPos = block.TOP_TIP;
                                    }
                                    break;
                                case WS:
                                case ES:
                                case triN:
                                case S:
                                    if (te.neighbourDown && !te.neighbourUp) {
                                        renderer.uvRotateTop = 2;
                                        iconYPos = block.TOP_LONG;
                                    }
                                    if (!te.neighbourDown && te.neighbourUp) {
                                        renderer.uvRotateBottom = 2;
                                        iconYNeg = block.BOTTOM_TIP;
                                    }
                                    break;
                                case NS:
                                    if (!te.neighbourDown && te.neighbourUp) {
                                        renderer.uvRotateBottom = 2;
                                        iconYNeg = block.BOTTOM_LONG;
                                    }
                                    if (te.neighbourDown && !te.neighbourUp) {
                                        renderer.uvRotateTop = 2;
                                        iconYPos = block.TOP_LONG;
                                    }
                                    break;
                                case triE:
                                    if (!te.neighbourDown && te.neighbourUp) {
                                        renderer.uvRotateBottom = 1;
                                        iconYNeg = block.BOTTOM_TRIPLE_UP;
                                    }
                                    if (te.neighbourDown && !te.neighbourUp) {
                                        renderer.uvRotateTop = 2;
                                        iconYPos = block.TOP_TRIPLE_UP;
                                    }
                                    break;
                                case triW:
                                    if (!te.neighbourDown && te.neighbourUp) {
                                        renderer.uvRotateBottom = 2;
                                        iconYNeg = block.BOTTOM_TRIPLE_UP;
                                    }
                                    if (te.neighbourDown && !te.neighbourUp) {
                                        renderer.uvRotateTop = 1;
                                        iconYPos = block.TOP_TRIPLE_UP;
                                    }
                                    break;
                                case CROSS:
                                    if (!te.neighbourDown && te.neighbourUp) {
                                        iconYNeg = block.BOTTOM_CROSS;
                                    }
                                    if (te.neighbourDown && !te.neighbourUp) {
                                        iconYPos = block.TOP_CROSS;
                                    }
                                    break;
                            }

                            // Eastern & western faces

                            switch (te.GetConnectionType(2)) {
                                case N:
                                case triS:
                                    if (!te.neighbourEast) {
                                        renderer.uvRotateSouth = 1;
                                        iconXPos = block.TRIPLE_LEFT_UP_RB;
                                    }
                                    if (!te.neighbourWest) {
                                        renderer.uvRotateNorth = 2;
                                        iconXNeg = block.TRIPLE_LEFT_UP_RB;
                                    }
                                    break;
                                case S:
                                case triN:
                                    if (!te.neighbourEast) {
                                        renderer.uvRotateSouth = 1;
                                        iconXPos = block.TRIPLE_LEFT_DOWN_RB;
                                    }
                                    if (!te.neighbourWest) {
                                        renderer.uvRotateNorth = 2;
                                        iconXNeg = block.TRIPLE_LEFT_DOWN_RB;
                                    }
                                    break;
                                default:
                                case D:
                                case U:
                                case UD:
                                case NO:
                                    if (!te.neighbourEast) {
                                        renderer.uvRotateSouth = 1;
                                        iconXPos = block.LEFT_SIDE;
                                    }
                                    if (!te.neighbourWest) {
                                        renderer.uvRotateNorth = 2;
                                        iconXNeg = block.LEFT_SIDE;
                                    }
                                    break;
                                case UN:
                                    if (!te.neighbourEast) {
                                        renderer.uvRotateSouth = 0;
                                        iconXPos = block.RIGHT_UP_CLEAR;
                                    }
                                    if (!te.neighbourWest) {
                                        renderer.uvRotateNorth = 0;
                                        iconXNeg = block.LEFT_UP_CLEAR;
                                    }
                                    break;
                                case US:
                                    if (!te.neighbourEast) {
                                        renderer.uvRotateSouth = 0;
                                        iconXPos = block.LEFT_UP_ORNATED;
                                    }
                                    if (!te.neighbourWest) {
                                        renderer.uvRotateNorth = 0;
                                        iconXNeg = block.RIGHT_UP_ORNATED;
                                    }
                                    break;
                                case DN:
                                    if (!te.neighbourEast) {
                                        renderer.uvRotateSouth = 0;
                                        iconXPos = block.RIGHT_DOWN_CLEAR;
                                    }
                                    if (!te.neighbourWest) {
                                        renderer.uvRotateNorth = 0;
                                        iconXNeg = block.LEFT_DOWN_CLEAR;
                                    }
                                    break;
                                case DS:
                                    if (!te.neighbourEast) {
                                        renderer.uvRotateSouth = 0;
                                        iconXPos = block.LEFT_DOWN_ORNATED;
                                    }
                                    if (!te.neighbourWest) {
                                        renderer.uvRotateNorth = 0;
                                        iconXNeg = block.RIGHT_DOWN_ORNATED;
                                    }
                                    break;
                                case NS:
                                case CROSS:
                                    if (!te.neighbourEast) {
                                        renderer.uvRotateSouth = 0;
                                        iconXPos = block.LEFT_CROSS;
                                    }
                                    if (!te.neighbourWest) {
                                        renderer.uvRotateNorth = 0;
                                        iconXNeg = block.RIGHT_CROSS;
                                    }
                                    break;
                                case triU:
                                    if (!te.neighbourEast) {
                                        renderer.uvRotateSouth = 0;
                                        iconXPos = block.TRIPLE_LEFT_DOWN_RB;
                                    }
                                    if (!te.neighbourWest) {
                                        renderer.uvRotateNorth = 0;
                                        iconXNeg = block.TRIPLE_RIGHT_DOWN_LB;
                                    }
                                    break;
                                case triD:
                                    if (!te.neighbourEast) {
                                        renderer.uvRotateSouth = 0;
                                        iconXPos = block.TRIPLE_LEFT_UP_RB;
                                    }
                                    if (!te.neighbourWest) {
                                        renderer.uvRotateNorth = 0;
                                        iconXNeg = block.TRIPLE_RIGHT_UP_LB;
                                    }
                                    break;
                            }

                            // Northern & southern faces
                            if (te.neighbourSouth) iconZPos = block.CROSS_SECTION;
                            else {
                                if (!te.neighbourUp && te.neighbourDown && te.neighbourNorth) {
                                    renderer.uvRotateWest = 1;
                                    iconZPos = block.BOTTOM_TIP;
                                } else {
                                    renderer.uvRotateWest = 1;
                                    iconZPos = block.BOTTOM_LONG;
                                }
                            }
                            if (te.neighbourNorth) iconZNeg = block.CROSS_SECTION;
                            else {
                                if (!te.neighbourDown && te.neighbourUp && te.neighbourSouth) {
                                    renderer.uvRotateEast = 1;
                                    iconZNeg = block.TOP_TIP;
                                } else {
                                    renderer.uvRotateEast = 2;
                                    iconZNeg = block.TOP_LONG;
                                }
                            }

                            break;
                    }
                    break;
                default:
                case SIDE:
                    iconYNeg = block.BOTTOM_LONG;
                    iconYPos = block.TOP_LONG;
                    TileEntityTFNagastone upperTE = null;
                    Direction upperDirection = null;
                    Yaw upperFacing = null;
                    if (te.neighbourUp) {
                        upperTE = (TileEntityTFNagastone) world.getTileEntity(x, y + 1, z);
                        int upperMeta = world.getBlockMetadata(x, y + 1, z);
                        upperDirection = block.DirectionFromMeta(upperMeta);
                        upperFacing = block.FacingFromMeta(upperMeta);
                    }
                    TileEntityTFNagastone lowerTE = null;
                    Direction lowerDirection = null;
                    Yaw lowerFacing = null;
                    if (te.neighbourDown) {
                        lowerTE = (TileEntityTFNagastone) world.getTileEntity(x, y - 1, z);
                        int lowerMeta = world.getBlockMetadata(x, y - 1, z);
                        lowerDirection = block.DirectionFromMeta(lowerMeta);
                        lowerFacing = block.FacingFromMeta(lowerMeta);
                    }
                    switch (face) {
                        default:
                        case EAST:
                            // Lower & upper faces
                            renderer.uvRotateBottom = 3;
                            renderer.uvRotateTop = 3;
                            switch (te.GetConnectionType(0)) {
                                default:
                                    if (!(te.neighbourEast && te.neighbourWest)) {
                                        if (te.neighbourUp && !te.neighbourDown) {
                                            if (upperFacing == Yaw.EAST) {
                                                if (te.neighbourEast) {
                                                    renderer.uvRotateBottom = 0;
                                                    iconYNeg = block.BOTTOM_TIP;
                                                }
                                                if (te.neighbourWest) renderer.uvRotateBottom = 3;
                                            } else {
                                                if (te.neighbourEast) {
                                                    renderer.uvRotateBottom = 3;
                                                }
                                                if (te.neighbourWest) {
                                                    renderer.uvRotateBottom = 3;
                                                    iconYNeg = block.BOTTOM_TIP;
                                                }
                                            }
                                        }
                                        if (!te.neighbourUp && te.neighbourDown) {
                                            if (lowerFacing == Yaw.EAST) {
                                                if (te.neighbourEast) {
                                                    renderer.uvRotateTop = 3;
                                                }
                                                if (te.neighbourWest) {
                                                    renderer.uvRotateTop = 3;
                                                    iconYPos = block.TOP_TIP;
                                                }
                                            } else {
                                                if (te.neighbourEast) {
                                                    renderer.uvRotateTop = 0;
                                                    iconYPos = block.TOP_TIP;
                                                }
                                                if (te.neighbourWest) {
                                                    renderer.uvRotateTop = 0;
                                                }
                                            }
                                        }
                                    }
                                    break;
                                case N:
                                case triS:
                                    if (!te.neighbourDown) {
                                        renderer.uvRotateBottom = 3;
                                        iconYNeg = block.BOTTOM_TRIPLE_DOWN;
                                    }
                                    if (!te.neighbourUp) {
                                        renderer.uvRotateTop = 3;
                                        iconYPos = block.TOP_TRIPLE_DOWN;
                                    }
                                    break;
                                case S:
                                case triN:
                                    if (!te.neighbourDown) {
                                        renderer.uvRotateBottom = 0;
                                        iconYNeg = block.BOTTOM_TRIPLE_DOWN;
                                    }
                                    if (!te.neighbourUp) {
                                        renderer.uvRotateTop = 0;
                                        iconYPos = block.TOP_TRIPLE_DOWN;
                                    }
                                    break;
                                case WS:
                                    if (!te.neighbourDown) {
                                        renderer.uvRotateBottom = 1;
                                        iconYNeg = block.BOTTOM_ANGLE_UP;
                                    }
                                    if (!te.neighbourUp) {
                                        renderer.uvRotateTop = 2;
                                        iconYPos = block.TOP_ANGLE_UP;
                                    }
                                    break;
                                case ES:
                                    if (!te.neighbourDown) {
                                        renderer.uvRotateBottom = 3;
                                        iconYNeg = block.BOTTOM_ANGLE_UP;
                                    }
                                    if (!te.neighbourUp) {
                                        renderer.uvRotateTop = 3;
                                        iconYPos = block.TOP_ANGLE_UP;
                                    }
                                    break;
                                case WN:
                                    if (!te.neighbourDown) {
                                        renderer.uvRotateBottom = 0;
                                        iconYNeg = block.BOTTOM_ANGLE_UP;
                                    }
                                    if (!te.neighbourUp) {
                                        renderer.uvRotateTop = 0;
                                        iconYPos = block.TOP_ANGLE_UP;
                                    }
                                    break;
                                case EN:
                                    if (!te.neighbourDown) {
                                        renderer.uvRotateBottom = 2;
                                        iconYNeg = block.BOTTOM_ANGLE_UP;
                                    }
                                    if (!te.neighbourUp) {
                                        renderer.uvRotateTop = 1;
                                        iconYPos = block.TOP_ANGLE_UP;
                                    }
                                    break;
                                case triW:
                                    if (!te.neighbourDown) {
                                        renderer.uvRotateBottom = 2;
                                        iconYNeg = block.BOTTOM_TRIPLE_UP;
                                    }
                                    if (!te.neighbourUp) {
                                        renderer.uvRotateTop = 1;
                                        iconYPos = block.TOP_TRIPLE_UP;
                                    }
                                    break;
                                case triE:
                                    if (!te.neighbourDown) {
                                        renderer.uvRotateBottom = 1;
                                        iconYNeg = block.BOTTOM_TRIPLE_UP;
                                    }
                                    if (!te.neighbourUp) {
                                        renderer.uvRotateTop = 2;
                                        iconYPos = block.TOP_TRIPLE_UP;
                                    }
                                    break;
                                case NS:
                                case CROSS:
                                    if (!te.neighbourDown) {
                                        iconYNeg = block.BOTTOM_CROSS;
                                    }
                                    if (!te.neighbourUp) {
                                        iconYPos = block.TOP_CROSS;
                                    }
                                    break;
                            }

                            // Eastern & western faces
                            iconXPos = block.CROSS_SECTION;
                            iconXNeg = block.CROSS_SECTION;

                            switch (te.GetConnectionType(2)) {
                                default:
                                    break;
                                case UN:
                                case DN:
                                case N:
                                    if (!te.neighbourEast && te.neighbourWest) {
                                        renderer.uvRotateSouth = 0;
                                        iconXPos = block.LEFT_SIDE;
                                    }
                                    if (te.neighbourEast && !te.neighbourWest) {
                                        renderer.uvRotateNorth = 0;
                                        iconXNeg = block.LEFT_SIDE;
                                    }
                                    break;
                                case US:
                                case DS:
                                case S:
                                    if (!te.neighbourEast && te.neighbourWest) {
                                        renderer.uvRotateSouth = 0;
                                        iconXPos = block.RIGHT_SIDE;
                                    }
                                    if (te.neighbourEast && !te.neighbourWest) {
                                        renderer.uvRotateNorth = 0;
                                        iconXNeg = block.RIGHT_SIDE;
                                    }
                                    break;
                                case triN:
                                case triS:
                                case CROSS:
                                case UD:
                                    if (upperFacing == Yaw.EAST && lowerFacing == Yaw.EAST) {
                                        if (!te.neighbourEast && te.neighbourWest) {
                                            renderer.uvRotateSouth = 2;
                                            iconXPos = block.BOTTOM_LONG;
                                        }
                                        if (te.neighbourEast && !te.neighbourWest) {
                                            renderer.uvRotateNorth = 2;
                                            iconXNeg = block.TOP_LONG;
                                        }
                                        break;
                                    } else if (upperFacing == Yaw.WEST && lowerFacing == Yaw.WEST) {
                                        if (!te.neighbourEast && te.neighbourWest) {
                                            renderer.uvRotateSouth = 1;
                                            iconXPos = block.TOP_LONG;
                                        }
                                        if (te.neighbourEast && !te.neighbourWest) {
                                            renderer.uvRotateNorth = 1;
                                            iconXNeg = block.BOTTOM_LONG;
                                        }
                                        break;
                                    }
                                case U:
                                    if (upperFacing == Yaw.EAST) {
                                        if (!te.neighbourEast && te.neighbourWest) {
                                            renderer.uvRotateSouth = 2;
                                            iconXPos = block.BOTTOM_LONG;
                                        }
                                        if (te.neighbourEast && !te.neighbourWest) {
                                            renderer.uvRotateNorth = 2;
                                            iconXNeg = block.TOP_TIP;
                                        }
                                    } else {
                                        if (!te.neighbourEast && te.neighbourWest) {
                                            renderer.uvRotateSouth = 1;
                                            iconXPos = block.TOP_TIP;
                                        }
                                        if (te.neighbourEast && !te.neighbourWest) {
                                            renderer.uvRotateNorth = 2;
                                            iconXNeg = block.BOTTOM_LONG;
                                        }
                                    }
                                    break;
                                case D:
                                    if (lowerFacing == Yaw.EAST) {
                                        if (!te.neighbourEast && te.neighbourWest) {
                                            renderer.uvRotateSouth = 2;
                                            iconXPos = block.BOTTOM_TIP;
                                        }
                                        if (te.neighbourEast && !te.neighbourWest) {
                                            renderer.uvRotateNorth = 2;
                                            iconXNeg = block.TOP_LONG;
                                        }
                                    } else {
                                        if (!te.neighbourEast && te.neighbourWest) {
                                            renderer.uvRotateSouth = 1;
                                            iconXPos = block.TOP_LONG;
                                        }
                                        if (te.neighbourEast && !te.neighbourWest) {
                                            renderer.uvRotateNorth = 1;
                                            iconXNeg = block.BOTTOM_TIP;
                                        }
                                    }
                                    break;
                                case triU:
                                case triD:
                                case NS:
                                    if (!te.neighbourEast && te.neighbourWest) {
                                        renderer.uvRotateSouth = 0;
                                        iconXPos = block.LEFT_SIDE;
                                    }
                                    if (te.neighbourEast && !te.neighbourWest) {
                                        renderer.uvRotateNorth = 0;
                                        iconXNeg = block.RIGHT_SIDE;
                                    }
                                    break;
                            }

                            // Northern & southern faces
                            switch (te.GetConnectionType(4)) {
                                default:
                                    if (!te.neighbourSouth) {
                                        renderer.uvRotateWest = 0;
                                        iconZPos = block.LEFT_SIDE;
                                    }
                                    if (!te.neighbourNorth) {
                                        renderer.uvRotateEast = 0;
                                        iconZNeg = block.RIGHT_SIDE;
                                    }
                                    break;
                                case triE:
                                    if (upperFacing == Yaw.EAST && lowerFacing == Yaw.EAST) {
                                        if (!te.neighbourSouth) {
                                            renderer.uvRotateWest = 2;
                                            iconZPos = block.TRIPLE_LEFT_UP_RB;
                                        }
                                        if (!te.neighbourNorth) {
                                            renderer.uvRotateEast = 1;
                                            iconZNeg = block.TRIPLE_LEFT_UP_RB;
                                        }
                                        break;
                                    } else if (upperFacing == Yaw.WEST && lowerFacing == Yaw.WEST) {
                                        if (!te.neighbourSouth) {
                                            renderer.uvRotateWest = 1;
                                            iconZPos = block.TRIPLE_RIGHT_DOWN_LB;
                                        }
                                        if (!te.neighbourNorth) {
                                            renderer.uvRotateEast = 2;
                                            iconZNeg = block.TRIPLE_RIGHT_DOWN_LB;
                                        }
                                        break;
                                    }
                                case UW:
                                    if (upperFacing == Yaw.EAST) {
                                        if (!te.neighbourSouth) {
                                            renderer.uvRotateWest = 0;
                                            iconZPos = block.LEFT_UP_CLEAR;
                                        }
                                        if (!te.neighbourNorth) {
                                            renderer.uvRotateEast = 0;
                                            iconZNeg = block.RIGHT_UP_CLEAR;
                                        }
                                    } else {
                                        if (!te.neighbourSouth) {
                                            renderer.uvRotateWest = 0;
                                            iconZPos = block.LEFT_UP_ORNATED;
                                        }
                                        if (!te.neighbourNorth) {
                                            renderer.uvRotateEast = 0;
                                            iconZNeg = block.RIGHT_UP_ORNATED;
                                        }
                                    }
                                    break;
                                case triW:
                                    if (upperFacing == Yaw.EAST && lowerFacing == Yaw.EAST) {
                                        if (!te.neighbourSouth) {
                                            renderer.uvRotateWest = 2;
                                            iconZPos = block.TRIPLE_LEFT_DOWN_RB;
                                        }
                                        if (!te.neighbourNorth) {
                                            renderer.uvRotateEast = 1;
                                            iconZNeg = block.TRIPLE_LEFT_DOWN_RB;
                                        }
                                        break;
                                    } else if (upperFacing == Yaw.WEST && lowerFacing == Yaw.WEST) {
                                        if (!te.neighbourSouth) {
                                            renderer.uvRotateWest = 1;
                                            iconZPos = block.TRIPLE_RIGHT_UP_LB;
                                        }
                                        if (!te.neighbourNorth) {
                                            renderer.uvRotateEast = 2;
                                            iconZNeg = block.TRIPLE_RIGHT_UP_LB;
                                        }
                                        break;
                                    }
                                case UE:
                                    if (upperFacing == Yaw.EAST) {
                                        if (!te.neighbourSouth) {
                                            renderer.uvRotateWest = 0;
                                            iconZPos = block.RIGHT_UP_ORNATED;
                                        }
                                        if (!te.neighbourNorth) {
                                            renderer.uvRotateEast = 0;
                                            iconZNeg = block.LEFT_UP_ORNATED;
                                        }
                                    } else {
                                        if (!te.neighbourSouth) {
                                            renderer.uvRotateWest = 0;
                                            iconZPos = block.RIGHT_UP_CLEAR;
                                        }
                                        if (!te.neighbourNorth) {
                                            renderer.uvRotateEast = 0;
                                            iconZNeg = block.LEFT_UP_CLEAR;
                                        }
                                    }
                                    break;
                                case DW:
                                    if (lowerFacing == Yaw.EAST) {
                                        if (!te.neighbourSouth) {
                                            renderer.uvRotateWest = 0;
                                            iconZPos = block.LEFT_DOWN_CLEAR;
                                        }
                                        if (!te.neighbourNorth) {
                                            renderer.uvRotateEast = 0;
                                            iconZNeg = block.RIGHT_DOWN_CLEAR;
                                        }
                                    } else {
                                        if (!te.neighbourSouth) {
                                            renderer.uvRotateWest = 0;
                                            iconZPos = block.LEFT_DOWN_ORNATED;
                                        }
                                        if (!te.neighbourNorth) {
                                            renderer.uvRotateEast = 0;
                                            iconZNeg = block.RIGHT_DOWN_ORNATED;
                                        }
                                    }
                                    break;
                                case DE:
                                    if (lowerFacing == Yaw.EAST) {
                                        if (!te.neighbourSouth) {
                                            renderer.uvRotateWest = 0;
                                            iconZPos = block.RIGHT_DOWN_ORNATED;
                                        }
                                        if (!te.neighbourNorth) {
                                            renderer.uvRotateEast = 0;
                                            iconZNeg = block.LEFT_DOWN_ORNATED;
                                        }
                                    } else {
                                        if (!te.neighbourSouth) {
                                            renderer.uvRotateWest = 0;
                                            iconZPos = block.RIGHT_DOWN_CLEAR;
                                        }
                                        if (!te.neighbourNorth) {
                                            renderer.uvRotateEast = 0;
                                            iconZNeg = block.LEFT_DOWN_CLEAR;
                                        }
                                    }
                                    break;
                                case UD:
                                case CROSS:
                                    if (upperFacing == Yaw.EAST && lowerFacing == Yaw.EAST) {
                                        if (!te.neighbourSouth) {
                                            renderer.uvRotateWest = 2;
                                            iconZPos = block.LEFT_CROSS;
                                        }
                                        if (!te.neighbourNorth) {
                                            renderer.uvRotateEast = 1;
                                            iconZNeg = block.LEFT_CROSS;
                                        }
                                        break;
                                    } else if (upperFacing == Yaw.WEST && lowerFacing == Yaw.WEST) {
                                        if (!te.neighbourSouth) {
                                            renderer.uvRotateWest = 1;
                                            iconZPos = block.RIGHT_CROSS;
                                        }
                                        if (!te.neighbourNorth) {
                                            renderer.uvRotateEast = 2;
                                            iconZNeg = block.RIGHT_CROSS;
                                        }
                                        break;
                                    }
                                case U:
                                case triD:
                                    if (upperFacing == Yaw.EAST) {
                                        if (!te.neighbourSouth) {
                                            renderer.uvRotateWest = 0;
                                            iconZPos = block.TRIPLE_LEFT_UP_LB;
                                        }
                                        if (!te.neighbourNorth) {
                                            renderer.uvRotateEast = 0;
                                            iconZNeg = block.TRIPLE_RIGHT_UP_RB;
                                        }
                                    } else {
                                        if (!te.neighbourSouth) {
                                            renderer.uvRotateWest = 0;
                                            iconZPos = block.TRIPLE_LEFT_UP_RB;
                                        }
                                        if (!te.neighbourNorth) {
                                            renderer.uvRotateEast = 0;
                                            iconZNeg = block.TRIPLE_RIGHT_UP_LB;
                                        }
                                    }
                                    break;
                                case D:
                                case triU:
                                    if (lowerFacing == Yaw.EAST) {
                                        if (!te.neighbourSouth) {
                                            renderer.uvRotateWest = 0;
                                            iconZPos = block.TRIPLE_LEFT_DOWN_LB;
                                        }
                                        if (!te.neighbourNorth) {
                                            renderer.uvRotateEast = 0;
                                            iconZNeg = block.TRIPLE_RIGHT_DOWN_RB;
                                        }
                                    } else {
                                        if (!te.neighbourSouth) {
                                            renderer.uvRotateWest = 0;
                                            iconZPos = block.TRIPLE_LEFT_DOWN_RB;
                                        }
                                        if (!te.neighbourNorth) {
                                            renderer.uvRotateEast = 0;
                                            iconZNeg = block.TRIPLE_RIGHT_DOWN_LB;
                                        }
                                    }
                                    break;
                            }
                            break;
                        case WEST:
                            // Lower & upper faces
                            renderer.uvRotateBottom = 0;
                            renderer.uvRotateTop = 0;
                            switch (te.GetConnectionType(0)) {
                                default:
                                    if (!(te.neighbourEast && te.neighbourWest)) {
                                        if (te.neighbourUp && !te.neighbourDown) {
                                            if (upperFacing == Yaw.EAST) {
                                                if (te.neighbourEast) {
                                                    renderer.uvRotateBottom = 0;
                                                    iconYNeg = block.BOTTOM_TIP;
                                                }
                                                if (te.neighbourWest) renderer.uvRotateBottom = 0;
                                            } else {
                                                if (te.neighbourEast) {
                                                    renderer.uvRotateBottom = 0;
                                                }
                                                if (te.neighbourWest) {
                                                    renderer.uvRotateBottom = 3;
                                                    iconYNeg = block.BOTTOM_TIP;
                                                }
                                            }
                                        }
                                        if (!te.neighbourUp && te.neighbourDown) {
                                            if (lowerFacing == Yaw.EAST) {
                                                if (te.neighbourEast) {
                                                    renderer.uvRotateTop = 3;
                                                }
                                                if (te.neighbourWest) {
                                                    renderer.uvRotateTop = 3;
                                                    iconYPos = block.TOP_TIP;
                                                }
                                            } else {
                                                if (te.neighbourEast) {
                                                    renderer.uvRotateTop = 0;
                                                    iconYPos = block.TOP_TIP;
                                                }
                                                if (te.neighbourWest) {
                                                    renderer.uvRotateTop = 0;
                                                }
                                            }
                                        }
                                    }
                                    break;
                                case N:
                                case triS:
                                    if (!te.neighbourDown) {
                                        renderer.uvRotateBottom = 3;
                                        iconYNeg = block.BOTTOM_TRIPLE_DOWN;
                                    }
                                    if (!te.neighbourUp) {
                                        renderer.uvRotateTop = 3;
                                        iconYPos = block.TOP_TRIPLE_DOWN;
                                    }
                                    break;
                                case S:
                                case triN:
                                    if (!te.neighbourDown) {
                                        renderer.uvRotateBottom = 0;
                                        iconYNeg = block.BOTTOM_TRIPLE_DOWN;
                                    }
                                    if (!te.neighbourUp) {
                                        renderer.uvRotateTop = 0;
                                        iconYPos = block.TOP_TRIPLE_DOWN;
                                    }
                                    break;
                                case WS:
                                    if (!te.neighbourDown) {
                                        renderer.uvRotateBottom = 1;
                                        iconYNeg = block.BOTTOM_ANGLE_UP;
                                    }
                                    if (!te.neighbourUp) {
                                        renderer.uvRotateTop = 2;
                                        iconYPos = block.TOP_ANGLE_UP;
                                    }
                                    break;
                                case ES:
                                    if (!te.neighbourDown) {
                                        renderer.uvRotateBottom = 3;
                                        iconYNeg = block.BOTTOM_ANGLE_UP;
                                    }
                                    if (!te.neighbourUp) {
                                        renderer.uvRotateTop = 3;
                                        iconYPos = block.TOP_ANGLE_UP;
                                    }
                                    break;
                                case WN:
                                    if (!te.neighbourDown) {
                                        renderer.uvRotateBottom = 0;
                                        iconYNeg = block.BOTTOM_ANGLE_UP;
                                    }
                                    if (!te.neighbourUp) {
                                        renderer.uvRotateTop = 0;
                                        iconYPos = block.TOP_ANGLE_UP;
                                    }
                                    break;
                                case EN:
                                    if (!te.neighbourDown) {
                                        renderer.uvRotateBottom = 2;
                                        iconYNeg = block.BOTTOM_ANGLE_UP;
                                    }
                                    if (!te.neighbourUp) {
                                        renderer.uvRotateTop = 1;
                                        iconYPos = block.TOP_ANGLE_UP;
                                    }
                                    break;
                                case triW:
                                    if (!te.neighbourDown) {
                                        renderer.uvRotateBottom = 2;
                                        iconYNeg = block.BOTTOM_TRIPLE_UP;
                                    }
                                    if (!te.neighbourUp) {
                                        renderer.uvRotateTop = 1;
                                        iconYPos = block.TOP_TRIPLE_UP;
                                    }
                                    break;
                                case triE:
                                    if (!te.neighbourDown) {
                                        renderer.uvRotateBottom = 1;
                                        iconYNeg = block.BOTTOM_TRIPLE_UP;
                                    }
                                    if (!te.neighbourUp) {
                                        renderer.uvRotateTop = 2;
                                        iconYPos = block.TOP_TRIPLE_UP;
                                    }
                                    break;
                                case NS:
                                case CROSS:
                                    if (!te.neighbourDown) {
                                        iconYNeg = block.BOTTOM_CROSS;
                                    }
                                    if (!te.neighbourUp) {
                                        iconYPos = block.TOP_CROSS;
                                    }
                                    break;
                            }

                            // Eastern & western faces
                            iconXPos = block.CROSS_SECTION;
                            iconXNeg = block.CROSS_SECTION;

                            switch (te.GetConnectionType(2)) {
                                default:
                                    break;
                                case UN:
                                case DN:
                                case N:
                                    if (!te.neighbourEast && te.neighbourWest) {
                                        renderer.uvRotateSouth = 0;
                                        iconXPos = block.RIGHT_SIDE;
                                    }
                                    if (te.neighbourEast && !te.neighbourWest) {
                                        renderer.uvRotateNorth = 0;
                                        iconXNeg = block.RIGHT_SIDE;
                                    }
                                    break;
                                case US:
                                case DS:
                                case S:
                                    if (!te.neighbourEast && te.neighbourWest) {
                                        renderer.uvRotateSouth = 0;
                                        iconXPos = block.LEFT_SIDE;
                                    }
                                    if (te.neighbourEast && !te.neighbourWest) {
                                        renderer.uvRotateNorth = 0;
                                        iconXNeg = block.LEFT_SIDE;
                                    }
                                    break;
                                case triN:
                                case triS:
                                case CROSS:
                                case UD:
                                    if (upperFacing == Yaw.EAST && lowerFacing == Yaw.EAST) {
                                        if (!te.neighbourEast && te.neighbourWest) {
                                            renderer.uvRotateSouth = 2;
                                            iconXPos = block.BOTTOM_LONG;
                                        }
                                        if (te.neighbourEast && !te.neighbourWest) {
                                            renderer.uvRotateNorth = 2;
                                            iconXNeg = block.TOP_LONG;
                                        }
                                        break;
                                    } else if (upperFacing == Yaw.WEST && lowerFacing == Yaw.WEST) {
                                        if (!te.neighbourEast && te.neighbourWest) {
                                            renderer.uvRotateSouth = 1;
                                            iconXPos = block.TOP_LONG;
                                        }
                                        if (te.neighbourEast && !te.neighbourWest) {
                                            renderer.uvRotateNorth = 1;
                                            iconXNeg = block.BOTTOM_LONG;
                                        }
                                        break;
                                    }
                                case U:
                                    if (upperFacing == Yaw.EAST) {
                                        if (!te.neighbourEast && te.neighbourWest) {
                                            renderer.uvRotateSouth = 1;
                                            iconXPos = block.BOTTOM_LONG;
                                        }
                                        if (te.neighbourEast && !te.neighbourWest) {
                                            renderer.uvRotateNorth = 2;
                                            iconXNeg = block.TOP_TIP;
                                        }
                                    } else {
                                        if (!te.neighbourEast && te.neighbourWest) {
                                            renderer.uvRotateSouth = 1;
                                            iconXPos = block.TOP_TIP;
                                        }
                                        if (te.neighbourEast && !te.neighbourWest) {
                                            renderer.uvRotateNorth = 1;
                                            iconXNeg = block.BOTTOM_LONG;
                                        }
                                    }
                                    break;
                                case D:
                                    if (lowerFacing == Yaw.EAST) {
                                        if (!te.neighbourEast && te.neighbourWest) {
                                            renderer.uvRotateSouth = 2;
                                            iconXPos = block.BOTTOM_TIP;
                                        }
                                        if (te.neighbourEast && !te.neighbourWest) {
                                            renderer.uvRotateNorth = 2;
                                            iconXNeg = block.TOP_LONG;
                                        }
                                    } else {
                                        if (!te.neighbourEast && te.neighbourWest) {
                                            renderer.uvRotateSouth = 1;
                                            iconXPos = block.TOP_LONG;
                                        }
                                        if (te.neighbourEast && !te.neighbourWest) {
                                            renderer.uvRotateNorth = 1;
                                            iconXNeg = block.BOTTOM_TIP;
                                        }
                                    }
                                    break;
                                case triU:
                                case triD:
                                case NS:
                                    if (!te.neighbourEast && te.neighbourWest) {
                                        renderer.uvRotateSouth = 0;
                                        iconXPos = block.LEFT_SIDE;
                                    }
                                    if (te.neighbourEast && !te.neighbourWest) {
                                        renderer.uvRotateNorth = 0;
                                        iconXNeg = block.LEFT_SIDE;
                                    }
                                    break;
                            }

                            // Northern & southern faces
                            switch (te.GetConnectionType(4)) {
                                default:
                                case E:
                                case W:
                                case EW:
                                case NO:
                                    if (!te.neighbourSouth) {
                                        renderer.uvRotateWest = 0;
                                        iconZPos = block.RIGHT_SIDE;
                                    }
                                    if (!te.neighbourNorth) {
                                        renderer.uvRotateEast = 0;
                                        iconZNeg = block.LEFT_SIDE;
                                    }
                                    break;
                                case triE:
                                    if (upperFacing == Yaw.EAST && lowerFacing == Yaw.EAST) {
                                        if (!te.neighbourSouth) {
                                            renderer.uvRotateWest = 2;
                                            iconZPos = block.TRIPLE_LEFT_UP_RB;
                                        }
                                        if (!te.neighbourNorth) {
                                            renderer.uvRotateEast = 1;
                                            iconZNeg = block.TRIPLE_LEFT_UP_RB;
                                        }
                                        break;
                                    } else if (upperFacing == Yaw.WEST && lowerFacing == Yaw.WEST) {
                                        if (!te.neighbourSouth) {
                                            renderer.uvRotateWest = 1;
                                            iconZPos = block.TRIPLE_RIGHT_DOWN_LB;
                                        }
                                        if (!te.neighbourNorth) {
                                            renderer.uvRotateEast = 2;
                                            iconZNeg = block.TRIPLE_RIGHT_DOWN_LB;
                                        }
                                        break;
                                    }
                                case UW:
                                    if (upperFacing == Yaw.EAST) {
                                        if (!te.neighbourSouth) {
                                            renderer.uvRotateWest = 0;
                                            iconZPos = block.LEFT_UP_CLEAR;
                                        }
                                        if (!te.neighbourNorth) {
                                            renderer.uvRotateEast = 0;
                                            iconZNeg = block.RIGHT_UP_CLEAR;
                                        }
                                    } else {
                                        if (!te.neighbourSouth) {
                                            renderer.uvRotateWest = 0;
                                            iconZPos = block.LEFT_UP_ORNATED;
                                        }
                                        if (!te.neighbourNorth) {
                                            renderer.uvRotateEast = 0;
                                            iconZNeg = block.RIGHT_UP_ORNATED;
                                        }
                                    }
                                    break;
                                case triW:
                                    if (upperFacing == Yaw.EAST && lowerFacing == Yaw.EAST) {
                                        if (!te.neighbourSouth) {
                                            renderer.uvRotateWest = 2;
                                            iconZPos = block.TRIPLE_LEFT_DOWN_RB;
                                        }
                                        if (!te.neighbourNorth) {
                                            renderer.uvRotateEast = 1;
                                            iconZNeg = block.TRIPLE_LEFT_DOWN_RB;
                                        }
                                        break;
                                    } else if (upperFacing == Yaw.WEST && lowerFacing == Yaw.WEST) {
                                        if (!te.neighbourSouth) {
                                            renderer.uvRotateWest = 1;
                                            iconZPos = block.TRIPLE_RIGHT_UP_LB;
                                        }
                                        if (!te.neighbourNorth) {
                                            renderer.uvRotateEast = 2;
                                            iconZNeg = block.TRIPLE_RIGHT_UP_LB;
                                        }
                                        break;
                                    }
                                case UE:
                                    if (upperFacing == Yaw.EAST) {
                                        if (!te.neighbourSouth) {
                                            renderer.uvRotateWest = 0;
                                            iconZPos = block.RIGHT_UP_ORNATED;
                                        }
                                        if (!te.neighbourNorth) {
                                            renderer.uvRotateEast = 0;
                                            iconZNeg = block.LEFT_UP_ORNATED;
                                        }
                                    } else {
                                        if (!te.neighbourSouth) {
                                            renderer.uvRotateWest = 0;
                                            iconZPos = block.RIGHT_UP_CLEAR;
                                        }
                                        if (!te.neighbourNorth) {
                                            renderer.uvRotateEast = 0;
                                            iconZNeg = block.LEFT_UP_CLEAR;
                                        }
                                    }
                                    break;
                                case DW:
                                    if (lowerFacing == Yaw.EAST) {
                                        if (!te.neighbourSouth) {
                                            renderer.uvRotateWest = 0;
                                            iconZPos = block.LEFT_DOWN_CLEAR;
                                        }
                                        if (!te.neighbourNorth) {
                                            renderer.uvRotateEast = 0;
                                            iconZNeg = block.RIGHT_DOWN_CLEAR;
                                        }
                                    } else {
                                        if (!te.neighbourSouth) {
                                            renderer.uvRotateWest = 0;
                                            iconZPos = block.LEFT_DOWN_ORNATED;
                                        }
                                        if (!te.neighbourNorth) {
                                            renderer.uvRotateEast = 0;
                                            iconZNeg = block.RIGHT_DOWN_ORNATED;
                                        }
                                    }
                                    break;
                                case DE:
                                    if (lowerFacing == Yaw.EAST) {
                                        if (!te.neighbourSouth) {
                                            renderer.uvRotateWest = 0;
                                            iconZPos = block.RIGHT_DOWN_ORNATED;
                                        }
                                        if (!te.neighbourNorth) {
                                            renderer.uvRotateEast = 0;
                                            iconZNeg = block.LEFT_DOWN_ORNATED;
                                        }
                                    } else {
                                        if (!te.neighbourSouth) {
                                            renderer.uvRotateWest = 0;
                                            iconZPos = block.RIGHT_DOWN_CLEAR;
                                        }
                                        if (!te.neighbourNorth) {
                                            renderer.uvRotateEast = 0;
                                            iconZNeg = block.LEFT_DOWN_CLEAR;
                                        }
                                    }
                                    break;
                                case UD:
                                case CROSS:
                                    if (upperFacing == Yaw.EAST && lowerFacing == Yaw.EAST) {
                                        if (!te.neighbourSouth) {
                                            renderer.uvRotateWest = 2;
                                            iconZPos = block.LEFT_CROSS;
                                        }
                                        if (!te.neighbourNorth) {
                                            renderer.uvRotateEast = 1;
                                            iconZNeg = block.LEFT_CROSS;
                                        }
                                        break;
                                    } else if (upperFacing == Yaw.WEST && lowerFacing == Yaw.WEST) {
                                        if (!te.neighbourSouth) {
                                            renderer.uvRotateWest = 1;
                                            iconZPos = block.RIGHT_CROSS;
                                        }
                                        if (!te.neighbourNorth) {
                                            renderer.uvRotateEast = 2;
                                            iconZNeg = block.RIGHT_CROSS;
                                        }
                                        break;
                                    }
                                case U:
                                case triD:
                                    if (upperFacing == Yaw.EAST) {
                                        if (!te.neighbourSouth) {
                                            renderer.uvRotateWest = 0;
                                            iconZPos = block.TRIPLE_RIGHT_UP_LB;
                                        }
                                        if (!te.neighbourNorth) {
                                            renderer.uvRotateEast = 0;
                                            iconZNeg = block.TRIPLE_LEFT_UP_RB;
                                        }
                                    } else {
                                        if (!te.neighbourSouth) {
                                            renderer.uvRotateWest = 0;
                                            iconZPos = block.TRIPLE_RIGHT_UP_RB;
                                        }
                                        if (!te.neighbourNorth) {
                                            renderer.uvRotateEast = 0;
                                            iconZNeg = block.TRIPLE_LEFT_UP_LB;
                                        }
                                    }
                                    break;
                                case D:
                                case triU:
                                    if (lowerFacing == Yaw.EAST) {
                                        if (!te.neighbourSouth) {
                                            renderer.uvRotateWest = 0;
                                            iconZPos = block.TRIPLE_RIGHT_DOWN_LB;
                                        }
                                        if (!te.neighbourNorth) {
                                            renderer.uvRotateEast = 0;
                                            iconZNeg = block.TRIPLE_LEFT_DOWN_RB;
                                        }
                                    } else {
                                        if (!te.neighbourSouth) {
                                            renderer.uvRotateWest = 0;
                                            iconZPos = block.TRIPLE_RIGHT_DOWN_RB;
                                        }
                                        if (!te.neighbourNorth) {
                                            renderer.uvRotateEast = 0;
                                            iconZNeg = block.TRIPLE_LEFT_DOWN_LB;
                                        }
                                    }
                                    break;
                            }
                            break;
                        case NORTH:
                            // Lower & upper faces
                            renderer.uvRotateBottom = 2;
                            renderer.uvRotateTop = 2;
                            switch (te.GetConnectionType(0)) {
                                default:
                                    if (!(te.neighbourSouth && te.neighbourNorth)) {
                                        if (te.neighbourUp && !te.neighbourDown) {
                                            if (upperFacing == Yaw.SOUTH) {
                                                if (te.neighbourSouth) {
                                                    renderer.uvRotateBottom = 2;
                                                    iconYNeg = block.BOTTOM_TIP;
                                                }
                                                if (te.neighbourNorth) renderer.uvRotateBottom = 2;
                                            } else {
                                                if (te.neighbourSouth) {
                                                    renderer.uvRotateBottom = 2;
                                                }
                                                if (te.neighbourNorth) {
                                                    renderer.uvRotateBottom = 1;
                                                    iconYNeg = block.BOTTOM_TIP;
                                                }
                                            }
                                        }
                                        if (!te.neighbourUp && te.neighbourDown) {
                                            if (lowerFacing == Yaw.SOUTH) {
                                                if (te.neighbourSouth) {
                                                    renderer.uvRotateTop = 2;
                                                }
                                                if (te.neighbourNorth) {
                                                    renderer.uvRotateTop = 2;
                                                    iconYPos = block.TOP_TIP;
                                                }
                                            } else {
                                                if (te.neighbourSouth) {
                                                    renderer.uvRotateTop = 1;
                                                    iconYPos = block.TOP_TIP;
                                                }
                                                if (te.neighbourNorth) {
                                                    renderer.uvRotateTop = 2;
                                                }
                                            }
                                        }
                                    }
                                    break;
                                case W:
                                case triE:
                                    if (!te.neighbourDown) {
                                        renderer.uvRotateBottom = 2;
                                        iconYNeg = block.BOTTOM_TRIPLE_DOWN;
                                    }
                                    if (!te.neighbourUp) {
                                        renderer.uvRotateTop = 1;
                                        iconYPos = block.TOP_TRIPLE_DOWN;
                                    }
                                    break;
                                case E:
                                case triW:
                                    if (!te.neighbourDown) {
                                        renderer.uvRotateBottom = 1;
                                        iconYNeg = block.BOTTOM_TRIPLE_DOWN;
                                    }
                                    if (!te.neighbourUp) {
                                        renderer.uvRotateTop = 2;
                                        iconYPos = block.TOP_TRIPLE_DOWN;
                                    }
                                    break;
                                case WS:
                                    if (!te.neighbourDown) {
                                        renderer.uvRotateBottom = 1;
                                        iconYNeg = block.BOTTOM_ANGLE_UP;
                                    }
                                    if (!te.neighbourUp) {
                                        renderer.uvRotateTop = 2;
                                        iconYPos = block.TOP_ANGLE_UP;
                                    }
                                    break;
                                case ES:
                                    if (!te.neighbourDown) {
                                        renderer.uvRotateBottom = 3;
                                        iconYNeg = block.BOTTOM_ANGLE_UP;
                                    }
                                    if (!te.neighbourUp) {
                                        renderer.uvRotateTop = 3;
                                        iconYPos = block.TOP_ANGLE_UP;
                                    }
                                    break;
                                case WN:
                                    if (!te.neighbourDown) {
                                        renderer.uvRotateBottom = 0;
                                        iconYNeg = block.BOTTOM_ANGLE_UP;
                                    }
                                    if (!te.neighbourUp) {
                                        renderer.uvRotateTop = 0;
                                        iconYPos = block.TOP_ANGLE_UP;
                                    }
                                    break;
                                case EN:
                                    if (!te.neighbourDown) {
                                        renderer.uvRotateBottom = 2;
                                        iconYNeg = block.BOTTOM_ANGLE_UP;
                                    }
                                    if (!te.neighbourUp) {
                                        renderer.uvRotateTop = 1;
                                        iconYPos = block.TOP_ANGLE_UP;
                                    }
                                    break;
                                case triN:
                                    if (!te.neighbourDown) {
                                        renderer.uvRotateBottom = 3;
                                        iconYNeg = block.BOTTOM_TRIPLE_UP;
                                    }
                                    if (!te.neighbourUp) {
                                        renderer.uvRotateTop = 3;
                                        iconYPos = block.TOP_TRIPLE_UP;
                                    }
                                    break;
                                case triS:
                                    if (!te.neighbourDown) {
                                        renderer.uvRotateBottom = 0;
                                        iconYNeg = block.BOTTOM_TRIPLE_UP;
                                    }
                                    if (!te.neighbourUp) {
                                        renderer.uvRotateTop = 0;
                                        iconYPos = block.TOP_TRIPLE_UP;
                                    }
                                    break;
                                case EW:
                                case CROSS:
                                    if (!te.neighbourDown) {
                                        iconYNeg = block.BOTTOM_CROSS;
                                    }
                                    if (!te.neighbourUp) {
                                        iconYPos = block.TOP_CROSS;
                                    }
                                    break;
                            }

                            // Eastern & western faces
                            switch (te.GetConnectionType(2)) {
                                default:
                                    if (!te.neighbourEast) {
                                        renderer.uvRotateSouth = 0;
                                        iconXPos = block.LEFT_SIDE;
                                    }
                                    if (!te.neighbourWest) {
                                        renderer.uvRotateNorth = 0;
                                        iconXNeg = block.RIGHT_SIDE;
                                    }
                                    break;
                                case triS:
                                    if (upperFacing == Yaw.SOUTH && lowerFacing == Yaw.SOUTH) {
                                        if (!te.neighbourEast) {
                                            renderer.uvRotateSouth = 1;
                                            iconXPos = block.TRIPLE_LEFT_UP_RB;
                                        }
                                        if (!te.neighbourWest) {
                                            renderer.uvRotateNorth = 2;
                                            iconXNeg = block.TRIPLE_LEFT_UP_RB;
                                        }
                                        break;
                                    } else if (upperFacing == Yaw.NORTH && lowerFacing == Yaw.NORTH) {
                                        if (!te.neighbourEast) {
                                            renderer.uvRotateSouth = 2;
                                            iconXPos = block.TRIPLE_RIGHT_DOWN_LB;
                                        }
                                        if (!te.neighbourWest) {
                                            renderer.uvRotateNorth = 1;
                                            iconXNeg = block.TRIPLE_RIGHT_DOWN_LB;
                                        }
                                        break;
                                    }
                                case UN:
                                    if (upperFacing == Yaw.SOUTH) {
                                        if (!te.neighbourEast) {
                                            renderer.uvRotateSouth = 0;
                                            iconXPos = block.RIGHT_UP_CLEAR;
                                        }
                                        if (!te.neighbourWest) {
                                            renderer.uvRotateNorth = 0;
                                            iconXNeg = block.LEFT_UP_CLEAR;
                                        }
                                    } else {
                                        if (!te.neighbourEast) {
                                            renderer.uvRotateSouth = 0;
                                            iconXPos = block.RIGHT_UP_ORNATED;
                                        }
                                        if (!te.neighbourWest) {
                                            renderer.uvRotateNorth = 0;
                                            iconXNeg = block.LEFT_UP_ORNATED;
                                        }
                                    }
                                    break;
                                case triN:
                                    if (upperFacing == Yaw.SOUTH && lowerFacing == Yaw.SOUTH) {
                                        if (!te.neighbourEast) {
                                            renderer.uvRotateSouth = 1;
                                            iconXPos = block.TRIPLE_LEFT_DOWN_RB;
                                        }
                                        if (!te.neighbourWest) {
                                            renderer.uvRotateNorth = 2;
                                            iconXNeg = block.TRIPLE_LEFT_DOWN_RB;
                                        }
                                        break;
                                    } else if (upperFacing == Yaw.NORTH && lowerFacing == Yaw.NORTH) {
                                        if (!te.neighbourEast) {
                                            renderer.uvRotateSouth = 2;
                                            iconXPos = block.TRIPLE_RIGHT_UP_LB;
                                        }
                                        if (!te.neighbourWest) {
                                            renderer.uvRotateNorth = 1;
                                            iconXNeg = block.TRIPLE_RIGHT_UP_LB;
                                        }
                                        break;
                                    }
                                case US:
                                    if (upperFacing == Yaw.SOUTH) {
                                        if (!te.neighbourEast) {
                                            renderer.uvRotateSouth = 0;
                                            iconXPos = block.LEFT_UP_ORNATED;
                                        }
                                        if (!te.neighbourWest) {
                                            renderer.uvRotateNorth = 0;
                                            iconXNeg = block.RIGHT_UP_ORNATED;
                                        }
                                    } else {
                                        if (!te.neighbourEast) {
                                            renderer.uvRotateSouth = 0;
                                            iconXPos = block.LEFT_UP_CLEAR;
                                        }
                                        if (!te.neighbourWest) {
                                            renderer.uvRotateNorth = 0;
                                            iconXNeg = block.RIGHT_UP_CLEAR;
                                        }
                                    }
                                    break;
                                case DN:
                                    if (lowerFacing == Yaw.SOUTH) {
                                        if (!te.neighbourEast) {
                                            renderer.uvRotateSouth = 0;
                                            iconXPos = block.RIGHT_DOWN_CLEAR;
                                        }
                                        if (!te.neighbourWest) {
                                            renderer.uvRotateNorth = 0;
                                            iconXNeg = block.LEFT_DOWN_CLEAR;
                                        }
                                    } else {
                                        if (!te.neighbourEast) {
                                            renderer.uvRotateSouth = 0;
                                            iconXPos = block.RIGHT_DOWN_ORNATED;
                                        }
                                        if (!te.neighbourWest) {
                                            renderer.uvRotateNorth = 0;
                                            iconXNeg = block.LEFT_DOWN_ORNATED;
                                        }
                                    }
                                    break;
                                case DS:
                                    if (lowerFacing == Yaw.SOUTH) {
                                        if (!te.neighbourEast) {
                                            renderer.uvRotateSouth = 0;
                                            iconXPos = block.LEFT_DOWN_ORNATED;
                                        }
                                        if (!te.neighbourWest) {
                                            renderer.uvRotateNorth = 0;
                                            iconXNeg = block.RIGHT_DOWN_ORNATED;
                                        }
                                    } else {
                                        if (!te.neighbourEast) {
                                            renderer.uvRotateSouth = 0;
                                            iconXPos = block.LEFT_DOWN_CLEAR;
                                        }
                                        if (!te.neighbourWest) {
                                            renderer.uvRotateNorth = 0;
                                            iconXNeg = block.RIGHT_DOWN_CLEAR;
                                        }
                                    }
                                    break;
                                case UD:
                                case CROSS:
                                    if (upperFacing == Yaw.SOUTH && lowerFacing == Yaw.SOUTH) {
                                        if (!te.neighbourEast) {
                                            renderer.uvRotateSouth = 1;
                                            iconXPos = block.LEFT_CROSS;
                                        }
                                        if (!te.neighbourWest) {
                                            renderer.uvRotateNorth = 2;
                                            iconXNeg = block.LEFT_CROSS;
                                        }
                                        break;
                                    } else if (upperFacing == Yaw.NORTH && lowerFacing == Yaw.NORTH) {
                                        if (!te.neighbourEast) {
                                            renderer.uvRotateSouth = 2;
                                            iconXPos = block.RIGHT_CROSS;
                                        }
                                        if (!te.neighbourWest) {
                                            renderer.uvRotateNorth = 1;
                                            iconXNeg = block.RIGHT_CROSS;
                                        }
                                        break;
                                    }
                                case U:
                                case triD:
                                    if (upperFacing == Yaw.SOUTH) {
                                        if (!te.neighbourEast) {
                                            renderer.uvRotateSouth = 0;
                                            iconXPos = block.TRIPLE_LEFT_UP_RB;
                                        }
                                        if (!te.neighbourWest) {
                                            renderer.uvRotateNorth = 0;
                                            iconXNeg = block.TRIPLE_RIGHT_UP_LB;
                                        }
                                    } else {
                                        if (!te.neighbourEast) {
                                            renderer.uvRotateSouth = 0;
                                            iconXPos = block.TRIPLE_LEFT_UP_LB;
                                        }
                                        if (!te.neighbourWest) {
                                            renderer.uvRotateNorth = 0;
                                            iconXNeg = block.TRIPLE_RIGHT_UP_RB;
                                        }
                                    }
                                    break;
                                case D:
                                case triU:
                                    if (lowerFacing == Yaw.SOUTH) {
                                        if (!te.neighbourEast) {
                                            renderer.uvRotateSouth = 0;
                                            iconXPos = block.TRIPLE_LEFT_DOWN_RB;
                                        }
                                        if (!te.neighbourWest) {
                                            renderer.uvRotateNorth = 0;
                                            iconXNeg = block.TRIPLE_RIGHT_DOWN_LB;
                                        }
                                    } else {
                                        if (!te.neighbourEast) {
                                            renderer.uvRotateSouth = 0;
                                            iconXPos = block.TRIPLE_LEFT_DOWN_LB;
                                        }
                                        if (!te.neighbourWest) {
                                            renderer.uvRotateNorth = 0;
                                            iconXNeg = block.TRIPLE_RIGHT_DOWN_RB;
                                        }
                                    }
                                    break;
                            }

                            // Northern & southern faces
                            iconZPos = block.CROSS_SECTION;
                            iconZNeg = block.CROSS_SECTION;

                            switch (te.GetConnectionType(4)) {
                                default:
                                    break;
                                case UW:
                                case DW:
                                case W:
                                    if (!te.neighbourSouth && te.neighbourNorth) {
                                        renderer.uvRotateWest = 0;
                                        iconZPos = block.LEFT_SIDE;
                                    }
                                    if (te.neighbourSouth && !te.neighbourNorth) {
                                        renderer.uvRotateEast = 0;
                                        iconZNeg = block.LEFT_SIDE;
                                    }
                                    break;
                                case UE:
                                case DE:
                                case E:
                                    if (!te.neighbourSouth && te.neighbourNorth) {
                                        renderer.uvRotateWest = 0;
                                        iconZPos = block.RIGHT_SIDE;
                                    }
                                    if (te.neighbourSouth && !te.neighbourNorth) {
                                        renderer.uvRotateEast = 0;
                                        iconZNeg = block.RIGHT_SIDE;
                                    }
                                    break;
                                case triW:
                                case triE:
                                case CROSS:
                                case UD:
                                    if (upperFacing == Yaw.SOUTH && lowerFacing == Yaw.SOUTH) {
                                        if (!te.neighbourSouth && te.neighbourNorth) {
                                            renderer.uvRotateWest = 2;
                                            iconZPos = block.BOTTOM_LONG;
                                        }
                                        if (te.neighbourSouth && !te.neighbourNorth) {
                                            renderer.uvRotateEast = 2;
                                            iconZNeg = block.TOP_LONG;
                                        }
                                        break;
                                    } else if (upperFacing == Yaw.NORTH && lowerFacing == Yaw.NORTH) {
                                        if (!te.neighbourSouth && te.neighbourNorth) {
                                            renderer.uvRotateWest = 1;
                                            iconZPos = block.TOP_LONG;
                                        }
                                        if (te.neighbourSouth && !te.neighbourNorth) {
                                            renderer.uvRotateEast = 1;
                                            iconZNeg = block.BOTTOM_LONG;
                                        }
                                        break;
                                    }
                                case U:
                                    if (upperFacing == Yaw.SOUTH) {
                                        if (!te.neighbourSouth && te.neighbourNorth) {
                                            renderer.uvRotateWest = 2;
                                            iconZPos = block.BOTTOM_LONG;
                                        }
                                        if (te.neighbourSouth && !te.neighbourNorth) {
                                            renderer.uvRotateEast = 1;
                                            iconZNeg = block.TOP_TIP;
                                        }
                                    } else {
                                        if (!te.neighbourSouth && te.neighbourNorth) {
                                            renderer.uvRotateWest = 2;
                                            iconZPos = block.TOP_TIP;
                                        }
                                        if (te.neighbourSouth && !te.neighbourNorth) {
                                            renderer.uvRotateEast = 2;
                                            iconZNeg = block.BOTTOM_LONG;
                                        }
                                    }
                                    break;
                                case D:
                                    if (lowerFacing == Yaw.SOUTH) {
                                        if (!te.neighbourSouth && te.neighbourNorth) {
                                            renderer.uvRotateWest = 1;
                                            iconZPos = block.BOTTOM_TIP;
                                        }
                                        if (te.neighbourSouth && !te.neighbourNorth) {
                                            renderer.uvRotateEast = 1;
                                            iconZNeg = block.TOP_LONG;
                                        }
                                    } else {
                                        if (!te.neighbourSouth && te.neighbourNorth) {
                                            renderer.uvRotateWest = 2;
                                            iconZPos = block.TOP_LONG;
                                        }
                                        if (te.neighbourSouth && !te.neighbourNorth) {
                                            renderer.uvRotateEast = 2;
                                            iconZNeg = block.BOTTOM_TIP;
                                        }
                                    }
                                    break;
                                case triU:
                                case triD:
                                case EW:
                                    if (!te.neighbourSouth && te.neighbourNorth) {
                                        renderer.uvRotateWest = 0;
                                        iconZPos = block.LEFT_SIDE;
                                    }
                                    if (te.neighbourSouth && !te.neighbourNorth) {
                                        renderer.uvRotateEast = 0;
                                        iconZNeg = block.LEFT_SIDE;
                                    }
                                    break;
                            }
                            break;
                        case SOUTH:
                            // Lower & upper faces
                            renderer.uvRotateBottom = 1;
                            renderer.uvRotateTop = 1;
                            switch (te.GetConnectionType(0)) {
                                default:
                                    if (!(te.neighbourSouth && te.neighbourNorth)) {
                                        if (te.neighbourUp && !te.neighbourDown) {
                                            if (upperFacing == Yaw.SOUTH) {
                                                if (te.neighbourSouth) {
                                                    renderer.uvRotateBottom = 2;
                                                    iconYNeg = block.BOTTOM_TIP;
                                                }
                                            } else {
                                                if (te.neighbourNorth) {
                                                    renderer.uvRotateBottom = 1;
                                                    iconYNeg = block.BOTTOM_TIP;
                                                }
                                            }
                                        }
                                        if (!te.neighbourUp && te.neighbourDown) {
                                            if (lowerFacing == Yaw.SOUTH) {
                                                if (te.neighbourNorth) {
                                                    renderer.uvRotateTop = 2;
                                                    iconYPos = block.TOP_TIP;
                                                }
                                            } else {
                                                if (te.neighbourSouth) {
                                                    renderer.uvRotateTop = 1;
                                                    iconYPos = block.TOP_TIP;
                                                }
                                            }
                                        }
                                    }
                                    break;
                                case W:
                                case triE:
                                    if (!te.neighbourDown) {
                                        renderer.uvRotateBottom = 2;
                                        iconYNeg = block.BOTTOM_TRIPLE_DOWN;
                                    }
                                    if (!te.neighbourUp) {
                                        renderer.uvRotateTop = 1;
                                        iconYPos = block.TOP_TRIPLE_DOWN;
                                    }
                                    break;
                                case E:
                                case triW:
                                    if (!te.neighbourDown) {
                                        renderer.uvRotateBottom = 1;
                                        iconYNeg = block.BOTTOM_TRIPLE_DOWN;
                                    }
                                    if (!te.neighbourUp) {
                                        renderer.uvRotateTop = 2;
                                        iconYPos = block.TOP_TRIPLE_DOWN;
                                    }
                                    break;
                                case WS:
                                    if (!te.neighbourDown) {
                                        renderer.uvRotateBottom = 1;
                                        iconYNeg = block.BOTTOM_ANGLE_UP;
                                    }
                                    if (!te.neighbourUp) {
                                        renderer.uvRotateTop = 2;
                                        iconYPos = block.TOP_ANGLE_UP;
                                    }
                                    break;
                                case ES:
                                    if (!te.neighbourDown) {
                                        renderer.uvRotateBottom = 3;
                                        iconYNeg = block.BOTTOM_ANGLE_UP;
                                    }
                                    if (!te.neighbourUp) {
                                        renderer.uvRotateTop = 3;
                                        iconYPos = block.TOP_ANGLE_UP;
                                    }
                                    break;
                                case WN:
                                    if (!te.neighbourDown) {
                                        renderer.uvRotateBottom = 0;
                                        iconYNeg = block.BOTTOM_ANGLE_UP;
                                    }
                                    if (!te.neighbourUp) {
                                        renderer.uvRotateTop = 0;
                                        iconYPos = block.TOP_ANGLE_UP;
                                    }
                                    break;
                                case EN:
                                    if (!te.neighbourDown) {
                                        renderer.uvRotateBottom = 2;
                                        iconYNeg = block.BOTTOM_ANGLE_UP;
                                    }
                                    if (!te.neighbourUp) {
                                        renderer.uvRotateTop = 1;
                                        iconYPos = block.TOP_ANGLE_UP;
                                    }
                                    break;
                                case triN:
                                    if (!te.neighbourDown) {
                                        renderer.uvRotateBottom = 3;
                                        iconYNeg = block.BOTTOM_TRIPLE_UP;
                                    }
                                    if (!te.neighbourUp) {
                                        renderer.uvRotateTop = 3;
                                        iconYPos = block.TOP_TRIPLE_UP;
                                    }
                                    break;
                                case triS:
                                    if (!te.neighbourDown) {
                                        renderer.uvRotateBottom = 0;
                                        iconYNeg = block.BOTTOM_TRIPLE_UP;
                                    }
                                    if (!te.neighbourUp) {
                                        renderer.uvRotateTop = 0;
                                        iconYPos = block.TOP_TRIPLE_UP;
                                    }
                                    break;
                                case EW:
                                case CROSS:
                                    if (!te.neighbourDown) {
                                        iconYNeg = block.BOTTOM_CROSS;
                                    }
                                    if (!te.neighbourUp) {
                                        iconYPos = block.TOP_CROSS;
                                    }
                                    break;
                            }

                            // Eastern & western faces
                            switch (te.GetConnectionType(2)) {
                                default:
                                    if (!te.neighbourEast) {
                                        renderer.uvRotateSouth = 0;
                                        iconXPos = block.RIGHT_SIDE;
                                    }
                                    if (!te.neighbourWest) {
                                        renderer.uvRotateNorth = 0;
                                        iconXNeg = block.LEFT_SIDE;
                                    }
                                    break;
                                case triS:
                                    if (upperFacing == Yaw.SOUTH && lowerFacing == Yaw.SOUTH) {
                                        if (!te.neighbourEast) {
                                            renderer.uvRotateSouth = 1;
                                            iconXPos = block.TRIPLE_LEFT_UP_RB;
                                        }
                                        if (!te.neighbourWest) {
                                            renderer.uvRotateNorth = 2;
                                            iconXNeg = block.TRIPLE_LEFT_UP_RB;
                                        }
                                        break;
                                    } else if (upperFacing == Yaw.NORTH && lowerFacing == Yaw.NORTH) {
                                        if (!te.neighbourEast) {
                                            renderer.uvRotateSouth = 2;
                                            iconXPos = block.TRIPLE_RIGHT_DOWN_LB;
                                        }
                                        if (!te.neighbourWest) {
                                            renderer.uvRotateNorth = 1;
                                            iconXNeg = block.TRIPLE_RIGHT_DOWN_LB;
                                        }
                                        break;
                                    }
                                case UN:
                                    if (upperFacing == Yaw.SOUTH) {
                                        if (!te.neighbourEast) {
                                            renderer.uvRotateSouth = 0;
                                            iconXPos = block.RIGHT_UP_CLEAR;
                                        }
                                        if (!te.neighbourWest) {
                                            renderer.uvRotateNorth = 0;
                                            iconXNeg = block.LEFT_UP_CLEAR;
                                        }
                                    } else {
                                        if (!te.neighbourEast) {
                                            renderer.uvRotateSouth = 0;
                                            iconXPos = block.RIGHT_UP_ORNATED;
                                        }
                                        if (!te.neighbourWest) {
                                            renderer.uvRotateNorth = 0;
                                            iconXNeg = block.LEFT_UP_ORNATED;
                                        }
                                    }
                                    break;
                                case triN:
                                    if (upperFacing == Yaw.SOUTH && lowerFacing == Yaw.SOUTH) {
                                        if (!te.neighbourEast) {
                                            renderer.uvRotateSouth = 1;
                                            iconXPos = block.TRIPLE_LEFT_DOWN_RB;
                                        }
                                        if (!te.neighbourWest) {
                                            renderer.uvRotateNorth = 2;
                                            iconXNeg = block.TRIPLE_LEFT_DOWN_RB;
                                        }
                                        break;
                                    } else if (upperFacing == Yaw.NORTH && lowerFacing == Yaw.NORTH) {
                                        if (!te.neighbourEast) {
                                            renderer.uvRotateSouth = 2;
                                            iconXPos = block.TRIPLE_RIGHT_UP_LB;
                                        }
                                        if (!te.neighbourWest) {
                                            renderer.uvRotateNorth = 1;
                                            iconXNeg = block.TRIPLE_RIGHT_UP_LB;
                                        }
                                        break;
                                    }
                                case US:
                                    if (upperFacing == Yaw.SOUTH) {
                                        if (!te.neighbourEast) {
                                            renderer.uvRotateSouth = 0;
                                            iconXPos = block.LEFT_UP_ORNATED;
                                        }
                                        if (!te.neighbourWest) {
                                            renderer.uvRotateNorth = 0;
                                            iconXNeg = block.RIGHT_UP_ORNATED;
                                        }
                                    } else {
                                        if (!te.neighbourEast) {
                                            renderer.uvRotateSouth = 0;
                                            iconXPos = block.LEFT_UP_CLEAR;
                                        }
                                        if (!te.neighbourWest) {
                                            renderer.uvRotateNorth = 0;
                                            iconXNeg = block.RIGHT_UP_CLEAR;
                                        }
                                    }
                                    break;
                                case DN:
                                    if (lowerFacing == Yaw.SOUTH) {
                                        if (!te.neighbourEast) {
                                            renderer.uvRotateSouth = 0;
                                            iconXPos = block.RIGHT_DOWN_CLEAR;
                                        }
                                        if (!te.neighbourWest) {
                                            renderer.uvRotateNorth = 0;
                                            iconXNeg = block.LEFT_DOWN_CLEAR;
                                        }
                                    } else {
                                        if (!te.neighbourEast) {
                                            renderer.uvRotateSouth = 0;
                                            iconXPos = block.RIGHT_DOWN_ORNATED;
                                        }
                                        if (!te.neighbourWest) {
                                            renderer.uvRotateNorth = 0;
                                            iconXNeg = block.LEFT_DOWN_ORNATED;
                                        }
                                    }
                                    break;
                                case DS:
                                    if (lowerFacing == Yaw.SOUTH) {
                                        if (!te.neighbourEast) {
                                            renderer.uvRotateSouth = 0;
                                            iconXPos = block.LEFT_DOWN_ORNATED;
                                        }
                                        if (!te.neighbourWest) {
                                            renderer.uvRotateNorth = 0;
                                            iconXNeg = block.RIGHT_DOWN_ORNATED;
                                        }
                                    } else {
                                        if (!te.neighbourEast) {
                                            renderer.uvRotateSouth = 0;
                                            iconXPos = block.LEFT_DOWN_CLEAR;
                                        }
                                        if (!te.neighbourWest) {
                                            renderer.uvRotateNorth = 0;
                                            iconXNeg = block.RIGHT_DOWN_CLEAR;
                                        }
                                    }
                                    break;
                                case UD:
                                case CROSS:
                                    if (upperFacing == Yaw.SOUTH && lowerFacing == Yaw.SOUTH) {
                                        if (!te.neighbourEast) {
                                            renderer.uvRotateSouth = 1;
                                            iconXPos = block.LEFT_CROSS;
                                        }
                                        if (!te.neighbourWest) {
                                            renderer.uvRotateNorth = 2;
                                            iconXNeg = block.LEFT_CROSS;
                                        }
                                        break;
                                    } else if (upperFacing == Yaw.NORTH && lowerFacing == Yaw.NORTH) {
                                        if (!te.neighbourEast) {
                                            renderer.uvRotateSouth = 2;
                                            iconXPos = block.RIGHT_CROSS;
                                        }
                                        if (!te.neighbourWest) {
                                            renderer.uvRotateNorth = 1;
                                            iconXNeg = block.RIGHT_CROSS;
                                        }
                                        break;
                                    }
                                case U:
                                case triD:
                                    if (upperFacing == Yaw.SOUTH) {
                                        if (!te.neighbourEast) {
                                            renderer.uvRotateSouth = 0;
                                            iconXPos = block.TRIPLE_RIGHT_UP_RB;
                                        }
                                        if (!te.neighbourWest) {
                                            renderer.uvRotateNorth = 0;
                                            iconXNeg = block.TRIPLE_LEFT_UP_LB;
                                        }
                                    } else {
                                        if (!te.neighbourEast) {
                                            renderer.uvRotateSouth = 0;
                                            iconXPos = block.TRIPLE_RIGHT_UP_LB;
                                        }
                                        if (!te.neighbourWest) {
                                            renderer.uvRotateNorth = 0;
                                            iconXNeg = block.TRIPLE_LEFT_UP_RB;
                                        }
                                    }
                                    break;
                                case D:
                                case triU:
                                    if (lowerFacing == Yaw.SOUTH) {
                                        if (!te.neighbourEast) {
                                            renderer.uvRotateSouth = 0;
                                            iconXPos = block.TRIPLE_RIGHT_DOWN_RB;
                                        }
                                        if (!te.neighbourWest) {
                                            renderer.uvRotateNorth = 0;
                                            iconXNeg = block.TRIPLE_LEFT_DOWN_LB;
                                        }
                                    } else {
                                        if (!te.neighbourEast) {
                                            renderer.uvRotateSouth = 0;
                                            iconXPos = block.TRIPLE_RIGHT_DOWN_LB;
                                        }
                                        if (!te.neighbourWest) {
                                            renderer.uvRotateNorth = 0;
                                            iconXNeg = block.TRIPLE_LEFT_DOWN_RB;
                                        }
                                    }
                                    break;
                            }

                            // Northern & southern faces
                            iconZPos = block.CROSS_SECTION;
                            iconZNeg = block.CROSS_SECTION;

                            switch (te.GetConnectionType(4)) {
                                default:
                                    break;
                                case UW:
                                case DW:
                                case W:
                                    if (!te.neighbourSouth && te.neighbourNorth) {
                                        renderer.uvRotateWest = 0;
                                        iconZPos = block.RIGHT_SIDE;
                                    }
                                    if (te.neighbourSouth && !te.neighbourNorth) {
                                        renderer.uvRotateEast = 0;
                                        iconZNeg = block.RIGHT_SIDE;
                                    }
                                    break;
                                case UE:
                                case DE:
                                case E:
                                    if (!te.neighbourSouth && te.neighbourNorth) {
                                        renderer.uvRotateWest = 0;
                                        iconZPos = block.LEFT_SIDE;
                                    }
                                    if (te.neighbourSouth && !te.neighbourNorth) {
                                        renderer.uvRotateEast = 0;
                                        iconZNeg = block.LEFT_SIDE;
                                    }
                                    break;
                                case triW:
                                case triE:
                                case CROSS:
                                case UD:
                                    if (upperFacing == Yaw.SOUTH && lowerFacing == Yaw.SOUTH) {
                                        if (!te.neighbourSouth && te.neighbourNorth) {
                                            renderer.uvRotateWest = 1;
                                            iconZPos = block.BOTTOM_LONG;
                                        }
                                        if (te.neighbourSouth && !te.neighbourNorth) {
                                            renderer.uvRotateEast = 1;
                                            iconZNeg = block.TOP_LONG;
                                        }
                                        break;
                                    } else if (upperFacing == Yaw.NORTH && lowerFacing == Yaw.NORTH) {
                                        if (!te.neighbourSouth && te.neighbourNorth) {
                                            renderer.uvRotateWest = 2;
                                            iconZPos = block.TOP_LONG;
                                        }
                                        if (te.neighbourSouth && !te.neighbourNorth) {
                                            renderer.uvRotateEast = 2;
                                            iconZNeg = block.BOTTOM_LONG;
                                        }
                                        break;
                                    }
                                case U:
                                    if (upperFacing == Yaw.SOUTH) {
                                        if (!te.neighbourSouth && te.neighbourNorth) {
                                            renderer.uvRotateWest = 1;
                                            iconZPos = block.BOTTOM_LONG;
                                        }
                                        if (te.neighbourSouth && !te.neighbourNorth) {
                                            renderer.uvRotateEast = 1;
                                            iconZNeg = block.TOP_TIP;
                                        }
                                    } else {
                                        if (!te.neighbourSouth && te.neighbourNorth) {
                                            renderer.uvRotateWest = 2;
                                            iconZPos = block.TOP_TIP;
                                        }
                                        if (te.neighbourSouth && !te.neighbourNorth) {
                                            renderer.uvRotateEast = 1;
                                            iconZNeg = block.BOTTOM_LONG;
                                        }
                                    }
                                    break;
                                case D:
                                    if (lowerFacing == Yaw.SOUTH) {
                                        if (!te.neighbourSouth && te.neighbourNorth) {
                                            renderer.uvRotateWest = 1;
                                            iconZPos = block.BOTTOM_TIP;
                                        }
                                        if (te.neighbourSouth && !te.neighbourNorth) {
                                            renderer.uvRotateEast = 2;
                                            iconZNeg = block.TOP_LONG;
                                        }
                                    } else {
                                        if (!te.neighbourSouth && te.neighbourNorth) {
                                            renderer.uvRotateWest = 1;
                                            iconZPos = block.TOP_LONG;
                                        }
                                        if (te.neighbourSouth && !te.neighbourNorth) {
                                            renderer.uvRotateEast = 2;
                                            iconZNeg = block.BOTTOM_TIP;
                                        }
                                    }
                                    break;
                                case triU:
                                case triD:
                                case EW:
                                    if (!te.neighbourSouth && te.neighbourNorth) {
                                        renderer.uvRotateWest = 0;
                                        iconZPos = block.LEFT_SIDE;
                                    }
                                    if (te.neighbourSouth && !te.neighbourNorth) {
                                        renderer.uvRotateEast = 0;
                                        iconZNeg = block.LEFT_SIDE;
                                    }
                                    break;
                            }
                            break;
                    }
                    break;
            }
        }

        float brightness = 1.0f;

        RenderXPos(renderer, block, x, y, z, brightness, brightness, brightness, iconXPos);
        RenderXNeg(renderer, block, x, y, z, brightness, brightness, brightness, iconXNeg);
        RenderZPos(renderer, block, x, y, z, brightness, brightness, brightness, iconZPos);
        RenderZNeg(renderer, block, x, y, z, brightness, brightness, brightness, iconZNeg);
        RenderYPos(renderer, block, x, y, z, brightness, brightness, brightness, iconYPos);
        RenderYNeg(renderer, block, x, y, z, brightness, brightness, brightness, iconYNeg);

        renderer.uvRotateEast = 0;
        renderer.uvRotateWest = 0;
        renderer.uvRotateSouth = 0;
        renderer.uvRotateNorth = 0;
        renderer.uvRotateTop = 0;
        renderer.uvRotateBottom = 0;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return true;
    }

    @Override
    public int getRenderId() {
        return renderID;
    }

    public static void renderInvNormalBlock(RenderBlocks renderblocks, Block par1Block, int meta) {
        Tessellator tessellator = Tessellator.instance;

        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        par1Block.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);

        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, -1.0F, 0.0F);
        renderblocks.uvRotateBottom = 3;
        renderblocks.renderFaceYNeg(par1Block, 0.0D, 0.0D, 0.0D, par1Block.getIcon(0, meta));
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        renderblocks.uvRotateTop = 3;
        renderblocks.renderFaceYPos(par1Block, 0.0D, 0.0D, 0.0D, par1Block.getIcon(1, meta));
        tessellator.setNormal(0.0F, 0.0F, -1.0F);
        renderblocks.renderFaceXPos(par1Block, 0.0D, 0.0D, 0.0D, par1Block.getIcon(2, meta));
        tessellator.setNormal(0.0F, 0.0F, 1.0F);
        renderblocks.renderFaceXNeg(par1Block, 0.0D, 0.0D, 0.0D, par1Block.getIcon(3, meta));
        tessellator.setNormal(-1.0F, 0.0F, 0.0F);
        renderblocks.renderFaceZNeg(par1Block, 0.0D, 0.0D, 0.0D, par1Block.getIcon(4, meta));
        tessellator.setNormal(1.0F, 0.0F, 0.0F);
        renderblocks.renderFaceZPos(par1Block, 0.0D, 0.0D, 0.0D, par1Block.getIcon(5, meta));
        tessellator.draw();
    }

    public boolean RenderYNeg(RenderBlocks renderer, Block block, int x, int y, int z, float red, float green,
            float blue, IIcon iicon) {
        renderer.enableAO = true;
        boolean flag = false;
        float f3 = 0.0F;
        float f4 = 0.0F;
        float f5 = 0.0F;
        float f6 = 0.0F;
        int l = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z);
        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(983055);
        int i1;
        float f7;

        if (renderer.renderAllFaces || block.shouldSideBeRendered(renderer.blockAccess, x, y - 1, z, 0)) {
            if (renderer.renderMinY <= 0.0D) {
                --y;
            }

            renderer.aoBrightnessXYNN = block.getMixedBrightnessForBlock(renderer.blockAccess, x - 1, y, z);
            renderer.aoBrightnessYZNN = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z - 1);
            renderer.aoBrightnessYZNP = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z + 1);
            renderer.aoBrightnessXYPN = block.getMixedBrightnessForBlock(renderer.blockAccess, x + 1, y, z);
            renderer.aoLightValueScratchXYNN = renderer.blockAccess.getBlock(x - 1, y, z)
                    .getAmbientOcclusionLightValue();
            renderer.aoLightValueScratchYZNN = renderer.blockAccess.getBlock(x, y, z - 1)
                    .getAmbientOcclusionLightValue();
            renderer.aoLightValueScratchYZNP = renderer.blockAccess.getBlock(x, y, z + 1)
                    .getAmbientOcclusionLightValue();
            renderer.aoLightValueScratchXYPN = renderer.blockAccess.getBlock(x + 1, y, z)
                    .getAmbientOcclusionLightValue();

            renderer.aoLightValueScratchXYZNNN = renderer.aoLightValueScratchXYNN;
            renderer.aoBrightnessXYZNNN = renderer.aoBrightnessXYNN;
            renderer.aoLightValueScratchXYZNNP = renderer.aoLightValueScratchXYNN;
            renderer.aoBrightnessXYZNNP = renderer.aoBrightnessXYNN;
            renderer.aoLightValueScratchXYZPNN = renderer.aoLightValueScratchXYPN;
            renderer.aoBrightnessXYZPNN = renderer.aoBrightnessXYPN;
            renderer.aoLightValueScratchXYZPNP = renderer.aoLightValueScratchXYPN;
            renderer.aoBrightnessXYZPNP = renderer.aoBrightnessXYPN;

            if (renderer.renderMinY <= 0.0D) {
                ++y;
            }

            i1 = l;

            if (renderer.renderMinY <= 0.0D || !renderer.blockAccess.getBlock(x, y - 1, z).isOpaqueCube()) {
                i1 = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y - 1, z);
            }

            f7 = renderer.blockAccess.getBlock(x, y - 1, z).getAmbientOcclusionLightValue();
            f3 = (renderer.aoLightValueScratchXYZNNP + renderer.aoLightValueScratchXYNN
                    + renderer.aoLightValueScratchYZNP
                    + f7) / 4.0F;
            f6 = (renderer.aoLightValueScratchYZNP + f7
                    + renderer.aoLightValueScratchXYZPNP
                    + renderer.aoLightValueScratchXYPN) / 4.0F;
            f5 = (f7 + renderer.aoLightValueScratchYZNN
                    + renderer.aoLightValueScratchXYPN
                    + renderer.aoLightValueScratchXYZPNN) / 4.0F;
            f4 = (renderer.aoLightValueScratchXYNN + renderer.aoLightValueScratchXYZNNN
                    + f7
                    + renderer.aoLightValueScratchYZNN) / 4.0F;
            renderer.brightnessTopLeft = renderer.getAoBrightness(
                    renderer.aoBrightnessXYZNNP,
                    renderer.aoBrightnessXYNN,
                    renderer.aoBrightnessYZNP,
                    i1);
            renderer.brightnessTopRight = renderer.getAoBrightness(
                    renderer.aoBrightnessYZNP,
                    renderer.aoBrightnessXYZPNP,
                    renderer.aoBrightnessXYPN,
                    i1);
            renderer.brightnessBottomRight = renderer.getAoBrightness(
                    renderer.aoBrightnessYZNN,
                    renderer.aoBrightnessXYPN,
                    renderer.aoBrightnessXYZPNN,
                    i1);
            renderer.brightnessBottomLeft = renderer.getAoBrightness(
                    renderer.aoBrightnessXYNN,
                    renderer.aoBrightnessXYZNNN,
                    renderer.aoBrightnessYZNN,
                    i1);

            renderer.colorRedTopLeft = renderer.colorRedBottomLeft = renderer.colorRedBottomRight = renderer.colorRedTopRight = red
                    * 0.5F;
            renderer.colorGreenTopLeft = renderer.colorGreenBottomLeft = renderer.colorGreenBottomRight = renderer.colorGreenTopRight = green
                    * 0.5F;
            renderer.colorBlueTopLeft = renderer.colorBlueBottomLeft = renderer.colorBlueBottomRight = renderer.colorBlueTopRight = blue
                    * 0.5F;

            renderer.colorRedTopLeft *= f3;
            renderer.colorGreenTopLeft *= f3;
            renderer.colorBlueTopLeft *= f3;
            renderer.colorRedBottomLeft *= f4;
            renderer.colorGreenBottomLeft *= f4;
            renderer.colorBlueBottomLeft *= f4;
            renderer.colorRedBottomRight *= f5;
            renderer.colorGreenBottomRight *= f5;
            renderer.colorBlueBottomRight *= f5;
            renderer.colorRedTopRight *= f6;
            renderer.colorGreenTopRight *= f6;
            renderer.colorBlueTopRight *= f6;
            renderer.renderFaceYNeg(block, (double) x, (double) y, (double) z, iicon);
            flag = true;
        }

        renderer.enableAO = false;
        return flag;
    }

    public boolean RenderYPos(RenderBlocks renderer, Block block, int x, int y, int z, float red, float green,
            float blue, IIcon iicon) {
        renderer.enableAO = true;
        boolean flag = false;
        float f3 = 0.0F;
        float f4 = 0.0F;
        float f5 = 0.0F;
        float f6 = 0.0F;
        int l = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z);
        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(983055);
        int i1;
        float f7;

        if (renderer.renderAllFaces || block.shouldSideBeRendered(renderer.blockAccess, x, y + 1, z, 1)) {
            if (renderer.renderMaxY >= 1.0D) {
                ++y;
            }

            renderer.aoBrightnessXYNP = block.getMixedBrightnessForBlock(renderer.blockAccess, x - 1, y, z);
            renderer.aoBrightnessXYPP = block.getMixedBrightnessForBlock(renderer.blockAccess, x + 1, y, z);
            renderer.aoBrightnessYZPN = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z - 1);
            renderer.aoBrightnessYZPP = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z + 1);
            renderer.aoLightValueScratchXYNP = renderer.blockAccess.getBlock(x - 1, y, z)
                    .getAmbientOcclusionLightValue();
            renderer.aoLightValueScratchXYPP = renderer.blockAccess.getBlock(x + 1, y, z)
                    .getAmbientOcclusionLightValue();
            renderer.aoLightValueScratchYZPN = renderer.blockAccess.getBlock(x, y, z - 1)
                    .getAmbientOcclusionLightValue();
            renderer.aoLightValueScratchYZPP = renderer.blockAccess.getBlock(x, y, z + 1)
                    .getAmbientOcclusionLightValue();

            renderer.aoLightValueScratchXYZNPN = renderer.aoLightValueScratchXYNP;
            renderer.aoBrightnessXYZNPN = renderer.aoBrightnessXYNP;
            renderer.aoLightValueScratchXYZPPN = renderer.aoLightValueScratchXYPP;
            renderer.aoBrightnessXYZPPN = renderer.aoBrightnessXYPP;
            renderer.aoLightValueScratchXYZNPP = renderer.aoLightValueScratchXYNP;
            renderer.aoBrightnessXYZNPP = renderer.aoBrightnessXYNP;
            renderer.aoLightValueScratchXYZPPP = renderer.aoLightValueScratchXYPP;
            renderer.aoBrightnessXYZPPP = renderer.aoBrightnessXYPP;

            if (renderer.renderMaxY >= 1.0D) {
                --y;
            }

            i1 = l;

            if (renderer.renderMaxY >= 1.0D || !renderer.blockAccess.getBlock(x, y + 1, z).isOpaqueCube()) {
                i1 = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y + 1, z);
            }

            f7 = renderer.blockAccess.getBlock(x, y + 1, z).getAmbientOcclusionLightValue();
            f6 = (renderer.aoLightValueScratchXYZNPP + renderer.aoLightValueScratchXYNP
                    + renderer.aoLightValueScratchYZPP
                    + f7) / 4.0F;
            f3 = (renderer.aoLightValueScratchYZPP + f7
                    + renderer.aoLightValueScratchXYZPPP
                    + renderer.aoLightValueScratchXYPP) / 4.0F;
            f4 = (f7 + renderer.aoLightValueScratchYZPN
                    + renderer.aoLightValueScratchXYPP
                    + renderer.aoLightValueScratchXYZPPN) / 4.0F;
            f5 = (renderer.aoLightValueScratchXYNP + renderer.aoLightValueScratchXYZNPN
                    + f7
                    + renderer.aoLightValueScratchYZPN) / 4.0F;
            renderer.brightnessTopRight = renderer.getAoBrightness(
                    renderer.aoBrightnessXYZNPP,
                    renderer.aoBrightnessXYNP,
                    renderer.aoBrightnessYZPP,
                    i1);
            renderer.brightnessTopLeft = renderer.getAoBrightness(
                    renderer.aoBrightnessYZPP,
                    renderer.aoBrightnessXYZPPP,
                    renderer.aoBrightnessXYPP,
                    i1);
            renderer.brightnessBottomLeft = renderer.getAoBrightness(
                    renderer.aoBrightnessYZPN,
                    renderer.aoBrightnessXYPP,
                    renderer.aoBrightnessXYZPPN,
                    i1);
            renderer.brightnessBottomRight = renderer.getAoBrightness(
                    renderer.aoBrightnessXYNP,
                    renderer.aoBrightnessXYZNPN,
                    renderer.aoBrightnessYZPN,
                    i1);
            renderer.colorRedTopLeft = renderer.colorRedBottomLeft = renderer.colorRedBottomRight = renderer.colorRedTopRight = red;
            renderer.colorGreenTopLeft = renderer.colorGreenBottomLeft = renderer.colorGreenBottomRight = renderer.colorGreenTopRight = green;
            renderer.colorBlueTopLeft = renderer.colorBlueBottomLeft = renderer.colorBlueBottomRight = renderer.colorBlueTopRight = blue;
            renderer.colorRedTopLeft *= f3;
            renderer.colorGreenTopLeft *= f3;
            renderer.colorBlueTopLeft *= f3;
            renderer.colorRedBottomLeft *= f4;
            renderer.colorGreenBottomLeft *= f4;
            renderer.colorBlueBottomLeft *= f4;
            renderer.colorRedBottomRight *= f5;
            renderer.colorGreenBottomRight *= f5;
            renderer.colorBlueBottomRight *= f5;
            renderer.colorRedTopRight *= f6;
            renderer.colorGreenTopRight *= f6;
            renderer.colorBlueTopRight *= f6;
            renderer.renderFaceYPos(block, (double) x, (double) y, (double) z, iicon);
            flag = true;
        }

        renderer.enableAO = false;
        return flag;
    }

    public boolean RenderXNeg(RenderBlocks renderer, Block block, int x, int y, int z, float red, float green,
            float blue, IIcon iicon) {
        renderer.enableAO = true;
        boolean flag = false;
        float f3 = 0.0F;
        float f4 = 0.0F;
        float f5 = 0.0F;
        float f6 = 0.0F;
        int l = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z);
        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(983055);
        int i1;
        float f7;
        float f8;
        float f9;
        float f10;
        float f11;
        int j1;
        int k1;
        int l1;
        int i2;

        if (renderer.renderAllFaces || block.shouldSideBeRendered(renderer.blockAccess, x - 1, y, z, 4)) {
            if (renderer.renderMinX <= 0.0D) {
                --x;
            }

            renderer.aoLightValueScratchXYNN = renderer.blockAccess.getBlock(x, y - 1, z)
                    .getAmbientOcclusionLightValue();
            renderer.aoLightValueScratchXZNN = renderer.blockAccess.getBlock(x, y, z - 1)
                    .getAmbientOcclusionLightValue();
            renderer.aoLightValueScratchXZNP = renderer.blockAccess.getBlock(x, y, z + 1)
                    .getAmbientOcclusionLightValue();
            renderer.aoLightValueScratchXYNP = renderer.blockAccess.getBlock(x, y + 1, z)
                    .getAmbientOcclusionLightValue();
            renderer.aoBrightnessXYNN = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y - 1, z);
            renderer.aoBrightnessXZNN = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z - 1);
            renderer.aoBrightnessXZNP = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z + 1);
            renderer.aoBrightnessXYNP = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y + 1, z);

            renderer.aoLightValueScratchXYZNNN = renderer.aoLightValueScratchXZNN;
            renderer.aoBrightnessXYZNNN = renderer.aoBrightnessXZNN;
            renderer.aoLightValueScratchXYZNNP = renderer.aoLightValueScratchXZNP;
            renderer.aoBrightnessXYZNNP = renderer.aoBrightnessXZNP;
            renderer.aoLightValueScratchXYZNPN = renderer.aoLightValueScratchXZNN;
            renderer.aoBrightnessXYZNPN = renderer.aoBrightnessXZNN;
            renderer.aoLightValueScratchXYZNPP = renderer.aoLightValueScratchXZNP;
            renderer.aoBrightnessXYZNPP = renderer.aoBrightnessXZNP;

            if (renderer.renderMinX <= 0.0D) {
                ++x;
            }

            i1 = l;

            if (renderer.renderMinX <= 0.0D || !renderer.blockAccess.getBlock(x - 1, y, z).isOpaqueCube()) {
                i1 = block.getMixedBrightnessForBlock(renderer.blockAccess, x - 1, y, z);
            }

            f7 = renderer.blockAccess.getBlock(x - 1, y, z).getAmbientOcclusionLightValue();
            f8 = (renderer.aoLightValueScratchXYNN + renderer.aoLightValueScratchXYZNNP
                    + f7
                    + renderer.aoLightValueScratchXZNP) / 4.0F;
            f9 = (f7 + renderer.aoLightValueScratchXZNP
                    + renderer.aoLightValueScratchXYNP
                    + renderer.aoLightValueScratchXYZNPP) / 4.0F;
            f10 = (renderer.aoLightValueScratchXZNN + f7
                    + renderer.aoLightValueScratchXYZNPN
                    + renderer.aoLightValueScratchXYNP) / 4.0F;
            f11 = (renderer.aoLightValueScratchXYZNNN + renderer.aoLightValueScratchXYNN
                    + renderer.aoLightValueScratchXZNN
                    + f7) / 4.0F;
            f3 = (float) ((double) f9 * renderer.renderMaxY * renderer.renderMaxZ
                    + (double) f10 * renderer.renderMaxY * (1.0D - renderer.renderMaxZ)
                    + (double) f11 * (1.0D - renderer.renderMaxY) * (1.0D - renderer.renderMaxZ)
                    + (double) f8 * (1.0D - renderer.renderMaxY) * renderer.renderMaxZ);
            f4 = (float) ((double) f9 * renderer.renderMaxY * renderer.renderMinZ
                    + (double) f10 * renderer.renderMaxY * (1.0D - renderer.renderMinZ)
                    + (double) f11 * (1.0D - renderer.renderMaxY) * (1.0D - renderer.renderMinZ)
                    + (double) f8 * (1.0D - renderer.renderMaxY) * renderer.renderMinZ);
            f5 = (float) ((double) f9 * renderer.renderMinY * renderer.renderMinZ
                    + (double) f10 * renderer.renderMinY * (1.0D - renderer.renderMinZ)
                    + (double) f11 * (1.0D - renderer.renderMinY) * (1.0D - renderer.renderMinZ)
                    + (double) f8 * (1.0D - renderer.renderMinY) * renderer.renderMinZ);
            f6 = (float) ((double) f9 * renderer.renderMinY * renderer.renderMaxZ
                    + (double) f10 * renderer.renderMinY * (1.0D - renderer.renderMaxZ)
                    + (double) f11 * (1.0D - renderer.renderMinY) * (1.0D - renderer.renderMaxZ)
                    + (double) f8 * (1.0D - renderer.renderMinY) * renderer.renderMaxZ);
            j1 = renderer.getAoBrightness(
                    renderer.aoBrightnessXYNN,
                    renderer.aoBrightnessXYZNNP,
                    renderer.aoBrightnessXZNP,
                    i1);
            k1 = renderer.getAoBrightness(
                    renderer.aoBrightnessXZNP,
                    renderer.aoBrightnessXYNP,
                    renderer.aoBrightnessXYZNPP,
                    i1);
            l1 = renderer.getAoBrightness(
                    renderer.aoBrightnessXZNN,
                    renderer.aoBrightnessXYZNPN,
                    renderer.aoBrightnessXYNP,
                    i1);
            i2 = renderer.getAoBrightness(
                    renderer.aoBrightnessXYZNNN,
                    renderer.aoBrightnessXYNN,
                    renderer.aoBrightnessXZNN,
                    i1);
            renderer.brightnessTopLeft = renderer.mixAoBrightness(
                    k1,
                    l1,
                    i2,
                    j1,
                    renderer.renderMaxY * renderer.renderMaxZ,
                    renderer.renderMaxY * (1.0D - renderer.renderMaxZ),
                    (1.0D - renderer.renderMaxY) * (1.0D - renderer.renderMaxZ),
                    (1.0D - renderer.renderMaxY) * renderer.renderMaxZ);
            renderer.brightnessBottomLeft = renderer.mixAoBrightness(
                    k1,
                    l1,
                    i2,
                    j1,
                    renderer.renderMaxY * renderer.renderMinZ,
                    renderer.renderMaxY * (1.0D - renderer.renderMinZ),
                    (1.0D - renderer.renderMaxY) * (1.0D - renderer.renderMinZ),
                    (1.0D - renderer.renderMaxY) * renderer.renderMinZ);
            renderer.brightnessBottomRight = renderer.mixAoBrightness(
                    k1,
                    l1,
                    i2,
                    j1,
                    renderer.renderMinY * renderer.renderMinZ,
                    renderer.renderMinY * (1.0D - renderer.renderMinZ),
                    (1.0D - renderer.renderMinY) * (1.0D - renderer.renderMinZ),
                    (1.0D - renderer.renderMinY) * renderer.renderMinZ);
            renderer.brightnessTopRight = renderer.mixAoBrightness(
                    k1,
                    l1,
                    i2,
                    j1,
                    renderer.renderMinY * renderer.renderMaxZ,
                    renderer.renderMinY * (1.0D - renderer.renderMaxZ),
                    (1.0D - renderer.renderMinY) * (1.0D - renderer.renderMaxZ),
                    (1.0D - renderer.renderMinY) * renderer.renderMaxZ);

            renderer.colorRedTopLeft = renderer.colorRedBottomLeft = renderer.colorRedBottomRight = renderer.colorRedTopRight = red
                    * 0.6F;
            renderer.colorGreenTopLeft = renderer.colorGreenBottomLeft = renderer.colorGreenBottomRight = renderer.colorGreenTopRight = green
                    * 0.6F;
            renderer.colorBlueTopLeft = renderer.colorBlueBottomLeft = renderer.colorBlueBottomRight = renderer.colorBlueTopRight = blue
                    * 0.6F;

            renderer.colorRedTopLeft *= f3;
            renderer.colorGreenTopLeft *= f3;
            renderer.colorBlueTopLeft *= f3;
            renderer.colorRedBottomLeft *= f4;
            renderer.colorGreenBottomLeft *= f4;
            renderer.colorBlueBottomLeft *= f4;
            renderer.colorRedBottomRight *= f5;
            renderer.colorGreenBottomRight *= f5;
            renderer.colorBlueBottomRight *= f5;
            renderer.colorRedTopRight *= f6;
            renderer.colorGreenTopRight *= f6;
            renderer.colorBlueTopRight *= f6;
            renderer.renderFaceXNeg(block, (double) x, (double) y, (double) z, iicon);

            flag = true;
        }

        renderer.enableAO = false;
        return flag;
    }

    public boolean RenderXPos(RenderBlocks renderer, Block block, int x, int y, int z, float red, float green,
            float blue, IIcon iicon) {
        renderer.enableAO = true;
        boolean flag = false;
        float f3 = 0.0F;
        float f4 = 0.0F;
        float f5 = 0.0F;
        float f6 = 0.0F;
        int l = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z);
        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(983055);
        int i1;
        float f7;
        float f8;
        float f9;
        float f10;
        float f11;
        int j1;
        int k1;
        int l1;
        int i2;

        if (renderer.renderAllFaces || block.shouldSideBeRendered(renderer.blockAccess, x + 1, y, z, 5)) {
            if (renderer.renderMaxX >= 1.0D) {
                ++x;
            }

            renderer.aoLightValueScratchXYPN = renderer.blockAccess.getBlock(x, y - 1, z)
                    .getAmbientOcclusionLightValue();
            renderer.aoLightValueScratchXZPN = renderer.blockAccess.getBlock(x, y, z - 1)
                    .getAmbientOcclusionLightValue();
            renderer.aoLightValueScratchXZPP = renderer.blockAccess.getBlock(x, y, z + 1)
                    .getAmbientOcclusionLightValue();
            renderer.aoLightValueScratchXYPP = renderer.blockAccess.getBlock(x, y + 1, z)
                    .getAmbientOcclusionLightValue();
            renderer.aoBrightnessXYPN = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y - 1, z);
            renderer.aoBrightnessXZPN = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z - 1);
            renderer.aoBrightnessXZPP = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z + 1);
            renderer.aoBrightnessXYPP = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y + 1, z);

            renderer.aoLightValueScratchXYZPNN = renderer.aoLightValueScratchXZPN;
            renderer.aoBrightnessXYZPNN = renderer.aoBrightnessXZPN;
            renderer.aoLightValueScratchXYZPNP = renderer.aoLightValueScratchXZPP;
            renderer.aoBrightnessXYZPNP = renderer.aoBrightnessXZPP;
            renderer.aoLightValueScratchXYZPPN = renderer.aoLightValueScratchXZPN;
            renderer.aoBrightnessXYZPPN = renderer.aoBrightnessXZPN;
            renderer.aoLightValueScratchXYZPPP = renderer.aoLightValueScratchXZPP;
            renderer.aoBrightnessXYZPPP = renderer.aoBrightnessXZPP;

            if (renderer.renderMaxX >= 1.0D) {
                --x;
            }

            i1 = l;

            if (renderer.renderMaxX >= 1.0D || !renderer.blockAccess.getBlock(x + 1, y, z).isOpaqueCube()) {
                i1 = block.getMixedBrightnessForBlock(renderer.blockAccess, x + 1, y, z);
            }

            f7 = renderer.blockAccess.getBlock(x + 1, y, z).getAmbientOcclusionLightValue();
            f8 = (renderer.aoLightValueScratchXYPN + renderer.aoLightValueScratchXYZPNP
                    + f7
                    + renderer.aoLightValueScratchXZPP) / 4.0F;
            f9 = (renderer.aoLightValueScratchXYZPNN + renderer.aoLightValueScratchXYPN
                    + renderer.aoLightValueScratchXZPN
                    + f7) / 4.0F;
            f10 = (renderer.aoLightValueScratchXZPN + f7
                    + renderer.aoLightValueScratchXYZPPN
                    + renderer.aoLightValueScratchXYPP) / 4.0F;
            f11 = (f7 + renderer.aoLightValueScratchXZPP
                    + renderer.aoLightValueScratchXYPP
                    + renderer.aoLightValueScratchXYZPPP) / 4.0F;
            f3 = (float) ((double) f8 * (1.0D - renderer.renderMinY) * renderer.renderMaxZ
                    + (double) f9 * (1.0D - renderer.renderMinY) * (1.0D - renderer.renderMaxZ)
                    + (double) f10 * renderer.renderMinY * (1.0D - renderer.renderMaxZ)
                    + (double) f11 * renderer.renderMinY * renderer.renderMaxZ);
            f4 = (float) ((double) f8 * (1.0D - renderer.renderMinY) * renderer.renderMinZ
                    + (double) f9 * (1.0D - renderer.renderMinY) * (1.0D - renderer.renderMinZ)
                    + (double) f10 * renderer.renderMinY * (1.0D - renderer.renderMinZ)
                    + (double) f11 * renderer.renderMinY * renderer.renderMinZ);
            f5 = (float) ((double) f8 * (1.0D - renderer.renderMaxY) * renderer.renderMinZ
                    + (double) f9 * (1.0D - renderer.renderMaxY) * (1.0D - renderer.renderMinZ)
                    + (double) f10 * renderer.renderMaxY * (1.0D - renderer.renderMinZ)
                    + (double) f11 * renderer.renderMaxY * renderer.renderMinZ);
            f6 = (float) ((double) f8 * (1.0D - renderer.renderMaxY) * renderer.renderMaxZ
                    + (double) f9 * (1.0D - renderer.renderMaxY) * (1.0D - renderer.renderMaxZ)
                    + (double) f10 * renderer.renderMaxY * (1.0D - renderer.renderMaxZ)
                    + (double) f11 * renderer.renderMaxY * renderer.renderMaxZ);
            j1 = renderer.getAoBrightness(
                    renderer.aoBrightnessXYPN,
                    renderer.aoBrightnessXYZPNP,
                    renderer.aoBrightnessXZPP,
                    i1);
            k1 = renderer.getAoBrightness(
                    renderer.aoBrightnessXZPP,
                    renderer.aoBrightnessXYPP,
                    renderer.aoBrightnessXYZPPP,
                    i1);
            l1 = renderer.getAoBrightness(
                    renderer.aoBrightnessXZPN,
                    renderer.aoBrightnessXYZPPN,
                    renderer.aoBrightnessXYPP,
                    i1);
            i2 = renderer.getAoBrightness(
                    renderer.aoBrightnessXYZPNN,
                    renderer.aoBrightnessXYPN,
                    renderer.aoBrightnessXZPN,
                    i1);
            renderer.brightnessTopLeft = renderer.mixAoBrightness(
                    j1,
                    i2,
                    l1,
                    k1,
                    (1.0D - renderer.renderMinY) * renderer.renderMaxZ,
                    (1.0D - renderer.renderMinY) * (1.0D - renderer.renderMaxZ),
                    renderer.renderMinY * (1.0D - renderer.renderMaxZ),
                    renderer.renderMinY * renderer.renderMaxZ);
            renderer.brightnessBottomLeft = renderer.mixAoBrightness(
                    j1,
                    i2,
                    l1,
                    k1,
                    (1.0D - renderer.renderMinY) * renderer.renderMinZ,
                    (1.0D - renderer.renderMinY) * (1.0D - renderer.renderMinZ),
                    renderer.renderMinY * (1.0D - renderer.renderMinZ),
                    renderer.renderMinY * renderer.renderMinZ);
            renderer.brightnessBottomRight = renderer.mixAoBrightness(
                    j1,
                    i2,
                    l1,
                    k1,
                    (1.0D - renderer.renderMaxY) * renderer.renderMinZ,
                    (1.0D - renderer.renderMaxY) * (1.0D - renderer.renderMinZ),
                    renderer.renderMaxY * (1.0D - renderer.renderMinZ),
                    renderer.renderMaxY * renderer.renderMinZ);
            renderer.brightnessTopRight = renderer.mixAoBrightness(
                    j1,
                    i2,
                    l1,
                    k1,
                    (1.0D - renderer.renderMaxY) * renderer.renderMaxZ,
                    (1.0D - renderer.renderMaxY) * (1.0D - renderer.renderMaxZ),
                    renderer.renderMaxY * (1.0D - renderer.renderMaxZ),
                    renderer.renderMaxY * renderer.renderMaxZ);

            renderer.colorRedTopLeft = renderer.colorRedBottomLeft = renderer.colorRedBottomRight = renderer.colorRedTopRight = red
                    * 0.6F;
            renderer.colorGreenTopLeft = renderer.colorGreenBottomLeft = renderer.colorGreenBottomRight = renderer.colorGreenTopRight = green
                    * 0.6F;
            renderer.colorBlueTopLeft = renderer.colorBlueBottomLeft = renderer.colorBlueBottomRight = renderer.colorBlueTopRight = blue
                    * 0.6F;

            renderer.colorRedTopLeft *= f3;
            renderer.colorGreenTopLeft *= f3;
            renderer.colorBlueTopLeft *= f3;
            renderer.colorRedBottomLeft *= f4;
            renderer.colorGreenBottomLeft *= f4;
            renderer.colorBlueBottomLeft *= f4;
            renderer.colorRedBottomRight *= f5;
            renderer.colorGreenBottomRight *= f5;
            renderer.colorBlueBottomRight *= f5;
            renderer.colorRedTopRight *= f6;
            renderer.colorGreenTopRight *= f6;
            renderer.colorBlueTopRight *= f6;
            renderer.renderFaceXPos(block, (double) x, (double) y, (double) z, iicon);

            flag = true;
        }

        renderer.enableAO = false;
        return flag;
    }

    public boolean RenderZNeg(RenderBlocks renderer, Block block, int x, int y, int z, float red, float green,
            float blue, IIcon iicon) {
        renderer.enableAO = true;
        boolean flag = false;
        float f3 = 0.0F;
        float f4 = 0.0F;
        float f5 = 0.0F;
        float f6 = 0.0F;
        int l = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z);
        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(983055);
        int i1;
        float f7;
        float f8;
        float f9;
        float f10;
        float f11;
        int j1;
        int k1;
        int l1;
        int i2;

        if (renderer.renderAllFaces || block.shouldSideBeRendered(renderer.blockAccess, x, y, z - 1, 2)) {
            if (renderer.renderMinZ <= 0.0D) {
                --z;
            }

            renderer.aoLightValueScratchXZNN = renderer.blockAccess.getBlock(x - 1, y, z)
                    .getAmbientOcclusionLightValue();
            renderer.aoLightValueScratchYZNN = renderer.blockAccess.getBlock(x, y - 1, z)
                    .getAmbientOcclusionLightValue();
            renderer.aoLightValueScratchYZPN = renderer.blockAccess.getBlock(x, y + 1, z)
                    .getAmbientOcclusionLightValue();
            renderer.aoLightValueScratchXZPN = renderer.blockAccess.getBlock(x + 1, y, z)
                    .getAmbientOcclusionLightValue();
            renderer.aoBrightnessXZNN = block.getMixedBrightnessForBlock(renderer.blockAccess, x - 1, y, z);
            renderer.aoBrightnessYZNN = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y - 1, z);
            renderer.aoBrightnessYZPN = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y + 1, z);
            renderer.aoBrightnessXZPN = block.getMixedBrightnessForBlock(renderer.blockAccess, x + 1, y, z);

            renderer.aoLightValueScratchXYZNNN = renderer.aoLightValueScratchXZNN;
            renderer.aoBrightnessXYZNNN = renderer.aoBrightnessXZNN;
            renderer.aoLightValueScratchXYZNPN = renderer.aoLightValueScratchXZNN;
            renderer.aoBrightnessXYZNPN = renderer.aoBrightnessXZNN;
            renderer.aoLightValueScratchXYZPNN = renderer.aoLightValueScratchXZPN;
            renderer.aoBrightnessXYZPNN = renderer.aoBrightnessXZPN;
            renderer.aoLightValueScratchXYZPPN = renderer.aoLightValueScratchXZPN;
            renderer.aoBrightnessXYZPPN = renderer.aoBrightnessXZPN;

            if (renderer.renderMinZ <= 0.0D) {
                ++z;
            }

            i1 = l;

            if (renderer.renderMinZ <= 0.0D || !renderer.blockAccess.getBlock(x, y, z - 1).isOpaqueCube()) {
                i1 = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z - 1);
            }

            f7 = renderer.blockAccess.getBlock(x, y, z - 1).getAmbientOcclusionLightValue();
            f8 = (renderer.aoLightValueScratchXZNN + renderer.aoLightValueScratchXYZNPN
                    + f7
                    + renderer.aoLightValueScratchYZPN) / 4.0F;
            f9 = (f7 + renderer.aoLightValueScratchYZPN
                    + renderer.aoLightValueScratchXZPN
                    + renderer.aoLightValueScratchXYZPPN) / 4.0F;
            f10 = (renderer.aoLightValueScratchYZNN + f7
                    + renderer.aoLightValueScratchXYZPNN
                    + renderer.aoLightValueScratchXZPN) / 4.0F;
            f11 = (renderer.aoLightValueScratchXYZNNN + renderer.aoLightValueScratchXZNN
                    + renderer.aoLightValueScratchYZNN
                    + f7) / 4.0F;
            f3 = (float) ((double) f8 * renderer.renderMaxY * (1.0D - renderer.renderMinX)
                    + (double) f9 * renderer.renderMaxY * renderer.renderMinX
                    + (double) f10 * (1.0D - renderer.renderMaxY) * renderer.renderMinX
                    + (double) f11 * (1.0D - renderer.renderMaxY) * (1.0D - renderer.renderMinX));
            f4 = (float) ((double) f8 * renderer.renderMaxY * (1.0D - renderer.renderMaxX)
                    + (double) f9 * renderer.renderMaxY * renderer.renderMaxX
                    + (double) f10 * (1.0D - renderer.renderMaxY) * renderer.renderMaxX
                    + (double) f11 * (1.0D - renderer.renderMaxY) * (1.0D - renderer.renderMaxX));
            f5 = (float) ((double) f8 * renderer.renderMinY * (1.0D - renderer.renderMaxX)
                    + (double) f9 * renderer.renderMinY * renderer.renderMaxX
                    + (double) f10 * (1.0D - renderer.renderMinY) * renderer.renderMaxX
                    + (double) f11 * (1.0D - renderer.renderMinY) * (1.0D - renderer.renderMaxX));
            f6 = (float) ((double) f8 * renderer.renderMinY * (1.0D - renderer.renderMinX)
                    + (double) f9 * renderer.renderMinY * renderer.renderMinX
                    + (double) f10 * (1.0D - renderer.renderMinY) * renderer.renderMinX
                    + (double) f11 * (1.0D - renderer.renderMinY) * (1.0D - renderer.renderMinX));
            j1 = renderer.getAoBrightness(
                    renderer.aoBrightnessXZNN,
                    renderer.aoBrightnessXYZNPN,
                    renderer.aoBrightnessYZPN,
                    i1);
            k1 = renderer.getAoBrightness(
                    renderer.aoBrightnessYZPN,
                    renderer.aoBrightnessXZPN,
                    renderer.aoBrightnessXYZPPN,
                    i1);
            l1 = renderer.getAoBrightness(
                    renderer.aoBrightnessYZNN,
                    renderer.aoBrightnessXYZPNN,
                    renderer.aoBrightnessXZPN,
                    i1);
            i2 = renderer.getAoBrightness(
                    renderer.aoBrightnessXYZNNN,
                    renderer.aoBrightnessXZNN,
                    renderer.aoBrightnessYZNN,
                    i1);
            renderer.brightnessTopLeft = renderer.mixAoBrightness(
                    j1,
                    k1,
                    l1,
                    i2,
                    renderer.renderMaxY * (1.0D - renderer.renderMinX),
                    renderer.renderMaxY * renderer.renderMinX,
                    (1.0D - renderer.renderMaxY) * renderer.renderMinX,
                    (1.0D - renderer.renderMaxY) * (1.0D - renderer.renderMinX));
            renderer.brightnessBottomLeft = renderer.mixAoBrightness(
                    j1,
                    k1,
                    l1,
                    i2,
                    renderer.renderMaxY * (1.0D - renderer.renderMaxX),
                    renderer.renderMaxY * renderer.renderMaxX,
                    (1.0D - renderer.renderMaxY) * renderer.renderMaxX,
                    (1.0D - renderer.renderMaxY) * (1.0D - renderer.renderMaxX));
            renderer.brightnessBottomRight = renderer.mixAoBrightness(
                    j1,
                    k1,
                    l1,
                    i2,
                    renderer.renderMinY * (1.0D - renderer.renderMaxX),
                    renderer.renderMinY * renderer.renderMaxX,
                    (1.0D - renderer.renderMinY) * renderer.renderMaxX,
                    (1.0D - renderer.renderMinY) * (1.0D - renderer.renderMaxX));
            renderer.brightnessTopRight = renderer.mixAoBrightness(
                    j1,
                    k1,
                    l1,
                    i2,
                    renderer.renderMinY * (1.0D - renderer.renderMinX),
                    renderer.renderMinY * renderer.renderMinX,
                    (1.0D - renderer.renderMinY) * renderer.renderMinX,
                    (1.0D - renderer.renderMinY) * (1.0D - renderer.renderMinX));

            renderer.colorRedTopLeft = renderer.colorRedBottomLeft = renderer.colorRedBottomRight = renderer.colorRedTopRight = red
                    * 0.8F;
            renderer.colorGreenTopLeft = renderer.colorGreenBottomLeft = renderer.colorGreenBottomRight = renderer.colorGreenTopRight = green
                    * 0.8F;
            renderer.colorBlueTopLeft = renderer.colorBlueBottomLeft = renderer.colorBlueBottomRight = renderer.colorBlueTopRight = blue
                    * 0.8F;

            renderer.colorRedTopLeft *= f3;
            renderer.colorGreenTopLeft *= f3;
            renderer.colorBlueTopLeft *= f3;
            renderer.colorRedBottomLeft *= f4;
            renderer.colorGreenBottomLeft *= f4;
            renderer.colorBlueBottomLeft *= f4;
            renderer.colorRedBottomRight *= f5;
            renderer.colorGreenBottomRight *= f5;
            renderer.colorBlueBottomRight *= f5;
            renderer.colorRedTopRight *= f6;
            renderer.colorGreenTopRight *= f6;
            renderer.colorBlueTopRight *= f6;
            renderer.renderFaceZNeg(block, (double) x, (double) y, (double) z, iicon);

            flag = true;
        }

        renderer.enableAO = false;
        return flag;
    }

    public boolean RenderZPos(RenderBlocks renderer, Block block, int x, int y, int z, float red, float green,
            float blue, IIcon iicon) {
        renderer.enableAO = true;
        boolean flag = false;
        float f3 = 0.0F;
        float f4 = 0.0F;
        float f5 = 0.0F;
        float f6 = 0.0F;
        int l = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z);
        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(983055);
        int i1;
        float f7;
        float f8;
        float f9;
        float f10;
        float f11;
        int j1;
        int k1;
        int l1;
        int i2;

        if (renderer.renderAllFaces || block.shouldSideBeRendered(renderer.blockAccess, x, y, z + 1, 3)) {
            if (renderer.renderMaxZ >= 1.0D) {
                ++z;
            }

            renderer.aoLightValueScratchXZNP = renderer.blockAccess.getBlock(x - 1, y, z)
                    .getAmbientOcclusionLightValue();
            renderer.aoLightValueScratchXZPP = renderer.blockAccess.getBlock(x + 1, y, z)
                    .getAmbientOcclusionLightValue();
            renderer.aoLightValueScratchYZNP = renderer.blockAccess.getBlock(x, y - 1, z)
                    .getAmbientOcclusionLightValue();
            renderer.aoLightValueScratchYZPP = renderer.blockAccess.getBlock(x, y + 1, z)
                    .getAmbientOcclusionLightValue();
            renderer.aoBrightnessXZNP = block.getMixedBrightnessForBlock(renderer.blockAccess, x - 1, y, z);
            renderer.aoBrightnessXZPP = block.getMixedBrightnessForBlock(renderer.blockAccess, x + 1, y, z);
            renderer.aoBrightnessYZNP = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y - 1, z);
            renderer.aoBrightnessYZPP = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y + 1, z);

            renderer.aoLightValueScratchXYZNNP = renderer.aoLightValueScratchXZNP;
            renderer.aoBrightnessXYZNNP = renderer.aoBrightnessXZNP;
            renderer.aoLightValueScratchXYZNPP = renderer.aoLightValueScratchXZNP;
            renderer.aoBrightnessXYZNPP = renderer.aoBrightnessXZNP;
            renderer.aoLightValueScratchXYZPNP = renderer.aoLightValueScratchXZPP;
            renderer.aoBrightnessXYZPNP = renderer.aoBrightnessXZPP;
            renderer.aoLightValueScratchXYZPPP = renderer.aoLightValueScratchXZPP;
            renderer.aoBrightnessXYZPPP = renderer.aoBrightnessXZPP;

            if (renderer.renderMaxZ >= 1.0D) {
                --z;
            }

            i1 = l;

            if (renderer.renderMaxZ >= 1.0D || !renderer.blockAccess.getBlock(x, y, z + 1).isOpaqueCube()) {
                i1 = block.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z + 1);
            }

            f7 = renderer.blockAccess.getBlock(x, y, z + 1).getAmbientOcclusionLightValue();
            f8 = (renderer.aoLightValueScratchXZNP + renderer.aoLightValueScratchXYZNPP
                    + f7
                    + renderer.aoLightValueScratchYZPP) / 4.0F;
            f9 = (f7 + renderer.aoLightValueScratchYZPP
                    + renderer.aoLightValueScratchXZPP
                    + renderer.aoLightValueScratchXYZPPP) / 4.0F;
            f10 = (renderer.aoLightValueScratchYZNP + f7
                    + renderer.aoLightValueScratchXYZPNP
                    + renderer.aoLightValueScratchXZPP) / 4.0F;
            f11 = (renderer.aoLightValueScratchXYZNNP + renderer.aoLightValueScratchXZNP
                    + renderer.aoLightValueScratchYZNP
                    + f7) / 4.0F;
            f3 = (float) ((double) f8 * renderer.renderMaxY * (1.0D - renderer.renderMinX)
                    + (double) f9 * renderer.renderMaxY * renderer.renderMinX
                    + (double) f10 * (1.0D - renderer.renderMaxY) * renderer.renderMinX
                    + (double) f11 * (1.0D - renderer.renderMaxY) * (1.0D - renderer.renderMinX));
            f4 = (float) ((double) f8 * renderer.renderMinY * (1.0D - renderer.renderMinX)
                    + (double) f9 * renderer.renderMinY * renderer.renderMinX
                    + (double) f10 * (1.0D - renderer.renderMinY) * renderer.renderMinX
                    + (double) f11 * (1.0D - renderer.renderMinY) * (1.0D - renderer.renderMinX));
            f5 = (float) ((double) f8 * renderer.renderMinY * (1.0D - renderer.renderMaxX)
                    + (double) f9 * renderer.renderMinY * renderer.renderMaxX
                    + (double) f10 * (1.0D - renderer.renderMinY) * renderer.renderMaxX
                    + (double) f11 * (1.0D - renderer.renderMinY) * (1.0D - renderer.renderMaxX));
            f6 = (float) ((double) f8 * renderer.renderMaxY * (1.0D - renderer.renderMaxX)
                    + (double) f9 * renderer.renderMaxY * renderer.renderMaxX
                    + (double) f10 * (1.0D - renderer.renderMaxY) * renderer.renderMaxX
                    + (double) f11 * (1.0D - renderer.renderMaxY) * (1.0D - renderer.renderMaxX));
            j1 = renderer.getAoBrightness(
                    renderer.aoBrightnessXZNP,
                    renderer.aoBrightnessXYZNPP,
                    renderer.aoBrightnessYZPP,
                    i1);
            k1 = renderer.getAoBrightness(
                    renderer.aoBrightnessYZPP,
                    renderer.aoBrightnessXZPP,
                    renderer.aoBrightnessXYZPPP,
                    i1);
            l1 = renderer.getAoBrightness(
                    renderer.aoBrightnessYZNP,
                    renderer.aoBrightnessXYZPNP,
                    renderer.aoBrightnessXZPP,
                    i1);
            i2 = renderer.getAoBrightness(
                    renderer.aoBrightnessXYZNNP,
                    renderer.aoBrightnessXZNP,
                    renderer.aoBrightnessYZNP,
                    i1);
            renderer.brightnessTopLeft = renderer.mixAoBrightness(
                    j1,
                    i2,
                    l1,
                    k1,
                    renderer.renderMaxY * (1.0D - renderer.renderMinX),
                    (1.0D - renderer.renderMaxY) * (1.0D - renderer.renderMinX),
                    (1.0D - renderer.renderMaxY) * renderer.renderMinX,
                    renderer.renderMaxY * renderer.renderMinX);
            renderer.brightnessBottomLeft = renderer.mixAoBrightness(
                    j1,
                    i2,
                    l1,
                    k1,
                    renderer.renderMinY * (1.0D - renderer.renderMinX),
                    (1.0D - renderer.renderMinY) * (1.0D - renderer.renderMinX),
                    (1.0D - renderer.renderMinY) * renderer.renderMinX,
                    renderer.renderMinY * renderer.renderMinX);
            renderer.brightnessBottomRight = renderer.mixAoBrightness(
                    j1,
                    i2,
                    l1,
                    k1,
                    renderer.renderMinY * (1.0D - renderer.renderMaxX),
                    (1.0D - renderer.renderMinY) * (1.0D - renderer.renderMaxX),
                    (1.0D - renderer.renderMinY) * renderer.renderMaxX,
                    renderer.renderMinY * renderer.renderMaxX);
            renderer.brightnessTopRight = renderer.mixAoBrightness(
                    j1,
                    i2,
                    l1,
                    k1,
                    renderer.renderMaxY * (1.0D - renderer.renderMaxX),
                    (1.0D - renderer.renderMaxY) * (1.0D - renderer.renderMaxX),
                    (1.0D - renderer.renderMaxY) * renderer.renderMaxX,
                    renderer.renderMaxY * renderer.renderMaxX);

            renderer.colorRedTopLeft = renderer.colorRedBottomLeft = renderer.colorRedBottomRight = renderer.colorRedTopRight = red
                    * 0.8F;
            renderer.colorGreenTopLeft = renderer.colorGreenBottomLeft = renderer.colorGreenBottomRight = renderer.colorGreenTopRight = green
                    * 0.8F;
            renderer.colorBlueTopLeft = renderer.colorBlueBottomLeft = renderer.colorBlueBottomRight = renderer.colorBlueTopRight = blue
                    * 0.8F;

            renderer.colorRedTopLeft *= f3;
            renderer.colorGreenTopLeft *= f3;
            renderer.colorBlueTopLeft *= f3;
            renderer.colorRedBottomLeft *= f4;
            renderer.colorGreenBottomLeft *= f4;
            renderer.colorBlueBottomLeft *= f4;
            renderer.colorRedBottomRight *= f5;
            renderer.colorGreenBottomRight *= f5;
            renderer.colorBlueBottomRight *= f5;
            renderer.colorRedTopRight *= f6;
            renderer.colorGreenTopRight *= f6;
            renderer.colorBlueTopRight *= f6;
            renderer.renderFaceZPos(block, (double) x, (double) y, (double) z, iicon);

            flag = true;
        }

        renderer.enableAO = false;
        return flag;
    }

}
