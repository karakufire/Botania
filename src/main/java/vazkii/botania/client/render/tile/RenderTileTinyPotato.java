/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 *
 * File Created @ [Jul 18, 2014, 10:48:46 PM (GMT)]
 */
package vazkii.botania.client.render.tile;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.MinecraftForge;
import vazkii.botania.api.item.TinyPotatoRenderEvent;
import vazkii.botania.api.state.BotaniaStateProps;
import vazkii.botania.client.core.handler.ContributorFancinessHandler;
import vazkii.botania.client.core.handler.MiscellaneousIcons;
import vazkii.botania.client.core.helper.IconHelper;
import vazkii.botania.client.core.helper.ShaderHelper;
import vazkii.botania.client.core.proxy.ClientProxy;
import vazkii.botania.client.lib.LibResources;
import vazkii.botania.client.model.ModelTinyPotato;
import vazkii.botania.common.block.ModBlocks;
import vazkii.botania.common.block.tile.TileTinyPotato;
import vazkii.botania.common.item.ModItems;
import vazkii.botania.common.item.block.ItemBlockTinyPotato;
import vazkii.botania.common.item.equipment.bauble.ItemFlightTiara;

import javax.annotation.Nonnull;

public class RenderTileTinyPotato extends TileEntityRenderer<TileTinyPotato> {
	private static final ResourceLocation texture = new ResourceLocation(LibResources.MODEL_TINY_POTATO);
	private static final ResourceLocation textureGrayscale = new ResourceLocation(LibResources.MODEL_TINY_POTATO_GS);
	private static final ResourceLocation textureHalloween = new ResourceLocation(LibResources.MODEL_TINY_POTATO_HALLOWEEN);
	private static final ModelTinyPotato model = new ModelTinyPotato();
	private final ModelRenderer potato = new ModelRenderer(16, 16, 0, 0);

	public RenderTileTinyPotato(TileEntityRendererDispatcher manager) {
		super(manager);
		potato.addCuboid(0F, 0F, 0F, 4, 6, 4);
		potato.setRotationPoint(-2F, 18F, -2F);
		potato.setTextureSize(64, 32);
	}

	private static boolean matches(String name, String match) {
		return name.equals(match) || name.startsWith(match + " ");
	}

	private static String removeFromFront(String name, String match) {
		return name.substring(match.length()).trim();
	}

	public Pair<ShaderHelper.BotaniaShader, String> stripShaderName(String name) {
		if (matches(name, "gaia")) {
			return Pair.of(ShaderHelper.BotaniaShader.DOPPLEGANGER, removeFromFront(name, "gaia"));
		} else if (matches(name, "hot")) {
			return Pair.of(ShaderHelper.BotaniaShader.HALO, removeFromFront(name, "hot"));
		} else if (matches(name, "magic")) {
			return Pair.of(ShaderHelper.BotaniaShader.ENCHANTER_RUNE, removeFromFront(name, "magic"));
		} else if (matches(name, "gold")) {
			return Pair.of(ShaderHelper.BotaniaShader.GOLD, removeFromFront(name, "gold"));
		} else if (matches(name, "snoop")) {
			return Pair.of(ShaderHelper.BotaniaShader.TERRA_PLATE, removeFromFront(name, "snoop"));
		} else {
			return Pair.of(null, name);
		}
	}

	public RenderType getRenderLayer(String name) {

	}

	@Override
	public void render(@Nonnull TileTinyPotato potato, float partialTicks, MatrixStack ms, IRenderTypeBuffer buffers, int light, int overlay) {
		if(potato.getBlockState().getBlock() != ModBlocks.tinyPotato)
			return;

		GlStateManager.pushMatrix();
		GlStateManager.enableRescaleNormal();
		GlStateManager.color4f(1F, 1F, 1F, 1F);
		GlStateManager.translated(x, y, z);

		Minecraft mc = Minecraft.getInstance();
		mc.textureManager.bindTexture(ClientProxy.dootDoot ? textureHalloween : texture);
		String name = potato.name.getString().toLowerCase().trim();

		boolean usedShader = false;


		GlStateManager.translatef(0.5F, 1.5F, 0.5F);
		GlStateManager.scalef(1F, -1F, -1F);
		Direction potatoFacing = potato.getBlockState().get(BotaniaStateProps.CARDINALS);
		float rotY = 0;
		switch(potatoFacing) {
			default:
			case SOUTH: break;
			case NORTH: rotY = 180F; break;
			case EAST: rotY = 270F; break;
			case WEST: rotY = 90F; break;
		}
		GlStateManager.rotatef(rotY, 0F, 1F, 0F);

		float jump = potato.jumpTicks;
		if (jump > 0)
			jump -= partialTicks;

		float up = (float) -Math.abs(Math.sin(jump / 10 * Math.PI)) * 0.2F;
		float rotZ = (float) Math.sin(jump / 10 * Math.PI) * 2;

		GlStateManager.translatef(0F, up, 0F);
		GlStateManager.rotatef(rotZ, 0F, 0F, 1F);

		GlStateManager.pushMatrix();
		switch (name) {
		case "pahimar":
			GlStateManager.scalef(1F, 0.3F, 1F);
			GlStateManager.translatef(0F, 3.5F, 0F);
			break;
		case "kyle hyde":
			mc.textureManager.bindTexture(textureGrayscale);
			break;
		case "dinnerbone":
		case "grumm":
			GlStateManager.rotatef(180F, 0F, 0F, 1F);
			GlStateManager.translatef(0F, -2.625F, 0F);
			break;
		case "aureylian":
			GlStateManager.color3f(1F, 0.5F, 1F);
			break;
		}

		boolean render = !(name.equals("mami") || name.equals("soaryn") || name.equals("eloraam") && jump != 0);
		if (render)
			model.render();
		if (name.equals("kingdaddydmac")) {
			GlStateManager.translatef(0.5F, 0F, 0F);
			model.render();
		}

		if (usedShader)
			ShaderHelper.releaseShader();

		GlStateManager.popMatrix();

		GlStateManager.pushMatrix();
		mc.textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);

		GlStateManager.pushMatrix();
		GlStateManager.rotatef(180F, 0, 0, 1);
		GlStateManager.translatef(0F, -1F, 0F);
		float s = 1F / 3.5F;
		GlStateManager.scalef(s, s, s);

		for(int i = 0; i < potato.getSizeInventory(); i++) {
			ItemStack stack = potato.getItemHandler().getStackInSlot(i);
			if(stack.isEmpty())
				continue;

			ms.push();
			Direction side = Direction.class.getEnumConstants()[i];
			if(side.getAxis() != Axis.Y) {
				float sideAngle = side.getHorizontalAngle() - potatoFacing.getHorizontalAngle();
				side = Direction.fromAngle(sideAngle);
			}

			boolean block = stack.getItem() instanceof BlockItem;
			boolean mySon = stack.getItem() instanceof ItemBlockTinyPotato;

			switch(side) {
			case UP:
				if(mySon)
					ms.translate(0F, 0.6F, 0.5F);
				else if(block)
					ms.translate(0F, 0.3F, 0.5F);
				ms.translate(0F, -0.5F, -0.4F);
				break;
			case DOWN:
				ms.translate(0, -2.3F, -0.88F);
				if(mySon)
					ms.translate(0, .65F, 0.6F);
				else if(block)
					ms.translate(0, 1, 0.6F);
				break;
			case NORTH:
				ms.translate(0, -1.9F, 0.02F);
				if(mySon)
					ms.translate(0, 1, 0.6F);
				else if(block)
					ms.translate(0, 1, 0.6F);
				break;
			case SOUTH:
				ms.translate(0, -1.6F, -0.89F);
				if(mySon)
					ms.translate(0, 1.4F, 0.5F);
				else if(block)
					ms.translate(0, 1.0F, 0.5F);
				break;
			case EAST:
				if(mySon)
					ms.translate(-0.4F, 0.65F, 0F);
				else if(block)
					ms.translate(-0.4F, 0.8F, 0F);
				else GlStateManager.rotatef(-90F, 0F, 1F, 0F);
				ms.translate(-0.3F, -1.9F, 0.04F);
				break;
			case WEST:
				if(mySon)
					ms.translate(1F, 0.65F, 1F);
				else if(block)
					ms.translate(1F, 0.8F, 1F);
				else GlStateManager.rotatef(-90F, 0F, 1F, 0F);
				ms.translate(-0.3F, -1.9F, -0.92F);
				break;
			}

			if (mySon)
				ms.scale(1.1F, 1.1F, 1.1F);
			else if(block)
				ms.scale(0.5F, 0.5F, 0.5F);
			if(block && side != Direction.NORTH)
				GlStateManager.rotatef(180F, 0, 1, 0);
			renderItem(ms, buffers, light, overlay, stack);
			ms.pop();
		}
		ms.pop();

		if (!name.isEmpty()) {
			ContributorFancinessHandler.firstStart();

			float scale = 1F / 4F;
			ms.translate(0F, 1F, 0F);
			ms.scale(scale, scale, scale);
			if (name.equals("phi") || name.equals("vazkii")) {
				ms.translate(0.45F, 0F, 0.4F);
				ms.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(90F));
				ms.multiply(new Vector3f(1, 0, 1).getDegreesQuaternion(20));
				renderModel(ms, buffers, light, overlay, MiscellaneousIcons.INSTANCE.phiFlowerModel);

				if (name.equals("vazkii")) {
					ms.multiply(new Vector3f(1, 0, 1).getDegreesQuaternion(-20));
					ms.scale(1.25F, 1.25F, 1.25F);
					ms.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(180F));
					ms.translate(-1.5F, -1.3F, -0.75F);
					renderModel(ms, buffers, light, overlay, MiscellaneousIcons.INSTANCE.nerfBatModel);
				}
			} else if (name.equals("haighyorkie")) {
				ms.scale(1.25F, 1.25F, 1.25F);
				ms.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(180F));
				ms.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(90F));
				ms.translate(-0.5F, -1.2F, -0.4F);
				renderModel(ms, buffers, light, overlay, MiscellaneousIcons.INSTANCE.goldfishModel);
			} else if (name.equals("martysgames") || name.equals("marty")) {
				ms.scale(0.7F, 0.7F, 0.7F);
				ms.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(180F));
				ms.translate(-0.3F, -2.7F, -1.2F);
				ms.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(15F));
				renderItem(ms, buffers, light, overlay, new ItemStack(ModItems.infiniteFruit, 1).setDisplayName(new StringTextComponent("das boot")));
			} else if (name.equals("jibril")) {
				GlStateManager.scalef(1.5F, 1.5F, 1.5F);
				GlStateManager.translatef(0F, -0.8F, 0F);
				GlStateManager.rotatef(90F, 0F, 1F, 0F);
				ItemFlightTiara.renderHalo(null, partialTicks);
				GlStateManager.disableBlend();
				GlStateManager.disableLighting();
			} else if (name.equals("kingdaddydmac")) {
				GlStateManager.scalef(0.5F, 0.5F, 0.5F);
				GlStateManager.rotatef(180F, 0F, 0F, 1F);
				GlStateManager.rotatef(90F, 0F, 1F, 0F);
				GlStateManager.translatef(0F, -3F, 0.65F);
				ItemStack ring = new ItemStack(ModItems.manaRing);
				renderItem(ms, buffers, light, overlay, ring);
				GlStateManager.translatef(0F, 0F, -4F);
				renderItem(ms, buffers, light, overlay, ring);

				GlStateManager.scalef(0.8F, 0.8F, 0.8F);
				GlStateManager.translatef(1.25F, -1.25F, 2.25F);
				renderBlock(ms, buffers, light, overlay, Blocks.CAKE);
			} else if (ContributorFancinessHandler.flowerMap.containsKey(name)) {
				ItemStack icon = ContributorFancinessHandler.flowerMap.getOrDefault(name, ItemStack.EMPTY);
				if (!icon.isEmpty()) {
					GlStateManager.rotatef(180F, 1F, 0F, 0F);
					GlStateManager.rotatef(180F, 0F, 1F, 0F);
					GlStateManager.translatef(0F, 0F, 0F);
					ShaderHelper.useShader(ShaderHelper.BotaniaShader.GOLD);
					renderItem(ms, buffers, light, overlay, icon);
					ShaderHelper.releaseShader();
				}
			}
		}
		GlStateManager.popMatrix();

		MinecraftForge.EVENT_BUS.post(new TinyPotatoRenderEvent(potato, potato.name, x, y, z, partialTicks, destroyStage));

		GlStateManager.rotatef(-rotZ, 0F, 0F, 1F);
		GlStateManager.rotatef(-rotY, 0F, 1F, 0F);
		GlStateManager.color3f(1F, 1F, 1F);
		GlStateManager.scalef(1F, -1F, -1F);

		RayTraceResult pos = mc.objectMouseOver;
		if (!name.isEmpty() && pos != null && pos.getType() == RayTraceResult.Type.BLOCK
				&& potato.getPos().equals(((BlockRayTraceResult) pos).getPos())) {
			GlStateManager.pushMatrix();
			GlStateManager.translatef(0F, -0.6F, 0F);
			GlStateManager.rotatef(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotatef(mc.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
			float f = 1.6F;
			float f1 = 0.016666668F * f;
			GlStateManager.scalef(-f1, -f1, f1);
			GlStateManager.disableLighting();
			GlStateManager.translatef(0.0F, 0F / f1, 0.0F);
			GlStateManager.depthMask(false);
			GlStateManager.enableBlend();
			GLX.glBlendFuncSeparate(770, 771, 1, 0);
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder worldrenderer = tessellator.getBuffer();
			GlStateManager.disableTexture();
			worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
			int i = mc.fontRenderer.getStringWidth(potato.name.getString()) / 2;
			worldrenderer.pos(-i - 1, -1.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
			worldrenderer.pos(-i - 1, 8.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
			worldrenderer.pos(i + 1, 8.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
			worldrenderer.pos(i + 1, -1.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
			tessellator.draw();
			GlStateManager.enableTexture();
			GlStateManager.depthMask(true);
			mc.fontRenderer.drawString(potato.name.getFormattedText(), -i, 0, 0xFFFFFF);
			if (name.equals("pahimar") || name.equals("soaryn")) {
				GlStateManager.translatef(0F, 14F, 0F);
				String str = name.equals("pahimar") ? "[WIP]" : "(soon)";
				GlStateManager.depthMask(false);
				GlStateManager.enableBlend();
				GLX.glBlendFuncSeparate(770, 771, 1, 0);
				GlStateManager.disableTexture();
				worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
				i = mc.fontRenderer.getStringWidth(str) / 2;
				worldrenderer.pos(-i - 1, -1.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
				worldrenderer.pos(-i - 1, 8.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
				worldrenderer.pos(i + 1, 8.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
				worldrenderer.pos(i + 1, -1.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
				tessellator.draw();
				GlStateManager.enableTexture();
				GlStateManager.depthMask(true);
				mc.fontRenderer.drawString(str, -mc.fontRenderer.getStringWidth(str) / 2, 0, 0xFFFFFF);
			}

			GlStateManager.enableLighting();
			GlStateManager.disableBlend();
			GlStateManager.color4f(1F, 1F, 1F, 1F);
			GlStateManager.scalef(1F / -f1, 1F / -f1, 1F / f1);
			GlStateManager.popMatrix();
		}

		GlStateManager.popMatrix();
	}

	private void renderModel(MatrixStack ms, IRenderTypeBuffer buffers, int light, int overlay, IBakedModel model) {
		Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelRenderer().render(ms.peek(), buffers.getBuffer(Atlases.getEntityTranslucent()), null, model, 1, 1, 1, light, overlay);
	}

	private void renderItem(MatrixStack ms, IRenderTypeBuffer buffers, int light, int overlay, ItemStack stack) {
		Minecraft.getInstance().getItemRenderer().renderItem(stack, ItemCameraTransforms.TransformType.HEAD, light, overlay, ms, buffers);
	}

	private void renderBlock(MatrixStack ms, IRenderTypeBuffer buffers, int light, int overlay, Block block) {
		IBakedModel model = Minecraft.getInstance().getBlockRendererDispatcher().getModelForState(block.getDefaultState());
		IVertexBuilder buffer = buffers.getBuffer(RenderTypeLookup.getBlockLayer(block.getDefaultState()));
		Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelRenderer().render(ms.peek(), buffer, null, model, 1, 1, 1, light, overlay);
	}
}
