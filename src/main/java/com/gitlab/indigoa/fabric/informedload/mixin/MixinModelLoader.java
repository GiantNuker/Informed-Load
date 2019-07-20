package com.gitlab.indigoa.fabric.informedload.mixin;

import com.gitlab.indigoa.fabric.informedload.TaskList;
import net.minecraft.block.Block;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Indigo Amann
 */
@Mixin(ModelLoader.class)
public class MixinModelLoader {
    @Shadow
    private Map<Identifier, UnbakedModel> modelsToBake;
    @Shadow
    private Map<Identifier, BakedModel> bakedModels;
    TaskList.Task.TaskAddModels taskAddModels = null;

    TaskList.Task.TaskBakeModels taskBakeModels;

    @Inject(method = "addModel(Lnet/minecraft/client/util/ModelIdentifier;)V", at = @At("RETURN"))
    private void onModelAddStart(ModelIdentifier modelIdentifier_1, CallbackInfo ci) {
        if (taskAddModels == null) { // Basically an init for this...
            if (!TaskList.hasTask("loadmodels")) TaskList.addTask(TaskList.Task.TaskLoadModels.INSTANCE);
            int items = 0;
            AtomicInteger blocks = new AtomicInteger();
            //Shamelessly stolen from ModelLoader
            Iterator var4 = Registry.BLOCK.iterator();

            while(var4.hasNext()) {
                Block block_1 = (Block)var4.next();
                block_1.getStateFactory().getStates().forEach((blockState_1) -> {
                    blocks.getAndIncrement();
                });
            }

            var4 = Registry.ITEM.getIds().iterator();

            while(var4.hasNext()) {
                Identifier identifier_1 = (Identifier)var4.next();
                items++;
            }
            taskAddModels = new TaskList.Task.TaskAddModels(items, blocks.get());
            TaskList.addTask(taskAddModels);
        } else {
            //System.out.println(TaskList.getTask("addmodels"));
        }
    }

    @Inject(method = "addModel(Lnet/minecraft/client/util/ModelIdentifier;)V", at = @At("RETURN"))
    private void onModelAddEnd(ModelIdentifier modelIdentifier_1, CallbackInfo ci) {
        //InformedLoad.taskAddModelss.remove(InformedLoad.taskAddModelss.size() - 1);
        String[] split = modelIdentifier_1.toString().split("#");
        //if (STATIC_DEFINITIONS.containsKey(new Identifier(split[0]))) {
        //    System.out.println("STATIC -- " + split[0]);
        //} else {
            if (Registry.BLOCK.containsId(new Identifier(split[0]))) {
                //System.out.println("Block contains " + modelIdentifier_1.toString().split("#")[0]);
                if (split.length == 1 || !split[1].equals("inventory")) taskAddModels.block();
                else if (Registry.ITEM.containsId(new Identifier(split[0]))) {
                    taskAddModels.item();
                }
            }
            else if (Registry.ITEM.containsId(new Identifier(split[0]))) {
                taskAddModels.item();
            }
            if (taskAddModels.items == taskAddModels.items_o && taskAddModels.blocks == taskAddModels.blocks_o) TaskList.removeTask("addmodels");
        //}
    }
    @Inject(method = "<init>", at = @At("RETURN"))
    private void initDone(ResourceManager resourceManager, SpriteAtlasTexture spriteAtlasTexture, BlockColors colors, Profiler profiler, CallbackInfo ci) {
        //TaskList.removeTask("addmodels");
    }
    @Inject(method = "upload(Lnet/minecraft/util/profiler/Profiler;)V", at = @At("HEAD"))
    private void listProgressUpload(Profiler profiler, CallbackInfo ci) {
        taskBakeModels = new TaskList.Task.TaskBakeModels(modelsToBake.size());
        TaskList.addTask(taskBakeModels);
    }

    @Inject(method = "bake(Lnet/minecraft/util/Identifier;Lnet/minecraft/client/render/model/ModelBakeSettings;)Lnet/minecraft/client/render/model/BakedModel;", at = @At("RETURN"))
    private void listProgressModelBake(Identifier identifier_1, ModelBakeSettings modelBakeSettings_1, CallbackInfoReturnable ci) {
        taskBakeModels.bake(bakedModels.size());
    }

}
