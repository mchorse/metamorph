package mchorse.metamorph.coremod.transform;

import java.util.ListIterator;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import mchorse.metamorph.coremod.MetamorphCoremod;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.util.SoundEvent;

public class TEntityPlayer implements IClassTransformer {
    public static final String ENTITY_PLAYER = "net.minecraft.entity.player.EntityPlayer";
    public static final String[] GET_HURT_SOUND = new String[]{"getHurtSound", "func_184601_bQ"};
    public static final String[] GET_DEATH_SOUND = new String[]{"getDeathSound", "func_184615_bR"};
    public static final String SOUND_EVENT = "net.minecraft.util.SoundEvent";
    
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass)
    {
        if (transformedName.equals(ENTITY_PLAYER))
        {
            return transformClass(basicClass, MetamorphCoremod.obfuscated);
        }
        return basicClass;
    }
    
    private byte[] transformClass(byte[] basicClass, boolean obfuscated)
    {
        ClassReader reader = new ClassReader(basicClass);
        ClassNode visitor = new ClassNode();
        reader.accept(visitor, 0);
        
        for (MethodNode method : visitor.methods)
        {
            if (method.name.equals(GET_HURT_SOUND[obfuscated ? 1 : 0]))
            {
                transformHurtSound(method);
            }
            else if (method.name.equals(GET_DEATH_SOUND[obfuscated ? 1 : 0]))
            {
                transformDeathSound(method);
            }
            //TODO: step sounds
        }
        
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        visitor.accept(writer);
        byte[] newClass = writer.toByteArray();
        
        return newClass;
    }
    
    private void transformHurtSound(MethodNode method)
    {
        InsnList instructions = method.instructions;
        for (AbstractInsnNode currentInsn = instructions.getFirst();
                currentInsn != null; currentInsn = currentInsn.getNext())
        {
            if (currentInsn.getOpcode() == Opcodes.ARETURN)
            {
                InsnList patch = new InsnList();
                // SoundEvent should be on the variable stack
                // this (EntityPlayer)
                patch.add(new VarInsnNode(Opcodes.ALOAD, 0));
                // SoundEvent SoundHooks.getHurtSound(SoundEvent defaultSound, EntityPlayer player)
                patch.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                        "mchorse/metamorph/client/SoundHooks",
                        "getHurtSound",
                        "(L" + SOUND_EVENT.replaceAll("\\.", "/") + ";" +
                            "L" + ENTITY_PLAYER.replaceAll("\\.", "/") + ";" +
                            ")L" + SOUND_EVENT.replaceAll("\\.", "/") + ";",
                        false));
                
                instructions.insertBefore(currentInsn, patch);
            }
        }
    }
    
    private void transformDeathSound(MethodNode method)
    {
        InsnList instructions = method.instructions;
        for (AbstractInsnNode currentInsn = instructions.getFirst();
                currentInsn != null; currentInsn = currentInsn.getNext())
        {
            if (currentInsn.getOpcode() == Opcodes.ARETURN)
            {
                InsnList patch = new InsnList();
                // SoundEvent should be on the variable stack
                // this (EntityPlayer)
                patch.add(new VarInsnNode(Opcodes.ALOAD, 0));
                // SoundEvent SoundHooks.getDeathSound(SoundEvent defaultSound, EntityPlayer player)
                patch.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                        "mchorse/metamorph/client/SoundHooks",
                        "getDeathSound",
                        "(L" + SOUND_EVENT.replaceAll("\\.", "/") + ";" +
                            "L" + ENTITY_PLAYER.replaceAll("\\.", "/") + ";" +
                            ")L" + SOUND_EVENT.replaceAll("\\.", "/") + ";",
                        false));
                
                instructions.insertBefore(currentInsn, patch);
            }
        }
    }
}
