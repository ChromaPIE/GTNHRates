package com.github.sladki.gtnhrates.mixins.late;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.github.sladki.gtnhrates.ModConfig;
import com.github.sladki.gtnhrates.Utils;

import bartworks.system.material.BWMetaGeneratedOres;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.Materials;
import gregtech.common.blocks.GTBlockOre;

@Mixin(value = GTBlockOre.class, remap = false)
public abstract class GTOres extends Block {

    @Unique
    private static final int gtnhrates$SMALL_ORE_META_OFFSET = 16000;

    protected GTOres(Material materialIn) {
        super(materialIn);
    }

    @Inject(method = "getDrops", at = @At(value = "RETURN"), cancellable = true)
    private void gtnhrates$onGetDrops(World world, int x, int y, int z, int metadata, int fortune,
        CallbackInfoReturnable<ArrayList<ItemStack>> cir) {

        EntityPlayer harvester = this.harvesters.get();
        boolean doSilktouch = harvester != null && EnchantmentHelper.getSilkTouchModifier(harvester);

        if (doSilktouch && metadata < gtnhrates$SMALL_ORE_META_OFFSET) {
            return;
        }

        float mult = ModConfig.Rates.gtOresDrops;

        Materials material = GregTechAPI.sGeneratedMaterials[metadata % 1000];
        if (material == Materials.Coal) {
            mult = ModConfig.Rates.gtCoalOreDrops;
        }

        cir.setReturnValue(Utils.multiplyItemStacksSize(cir.getReturnValue(), mult));
    }

    @Mixin(value = BWMetaGeneratedOres.class, remap = false)
    public abstract static class Bartworks extends Block {

        protected Bartworks(Material materialIn) {
            super(materialIn);
        }

        @Inject(method = "getDrops", at = @At(value = "RETURN"), cancellable = true)
        private void gtnhrates$onGetDrops(World world, int x, int y, int z, int metadata, int fortune,
            CallbackInfoReturnable<ArrayList<ItemStack>> cir) {

            EntityPlayer harvester = this.harvesters.get();
            boolean doSilktouch = harvester != null && EnchantmentHelper.getSilkTouchModifier(harvester);

            if (!doSilktouch) {
                cir.setReturnValue(Utils.multiplyItemStacksSize(cir.getReturnValue(), ModConfig.Rates.gtOresDrops));
            }
        }
    }
}
