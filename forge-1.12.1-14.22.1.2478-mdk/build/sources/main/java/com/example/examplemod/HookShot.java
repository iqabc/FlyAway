package com.example.examplemod;

import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;

import java.util.Timer;
import java.util.TimerTask;

public class HookShot extends ItemBow{
    private boolean fly = false;
    private boolean key[] = new boolean[4];
    private static final int S=0,W=1,A=2,D=3;
    private int upCount = 0;
    private final int period= 40;
    private static final double keyForce = 1.5d;
    private static final double wireForce = 3d;
    private double vx=0,vy=0,vz=0;//velocity
    private Vec3d des =  Vec3d.ZERO;
    private Vec3d acc = new Vec3d(0.1,0.1,0.1);//acceleration
    private Vec3d origin = new Vec3d(0.1,0.1,0.1);//original acceleration

    private Timer timer = null;
    private EntityPlayer mPlayer;

    public HookShot() {
        super();
        this.setCreativeTab(CreativeTabs.COMBAT);
        this.setUnlocalizedName("HookShot");
        this.setRegistryName("hookshot");
        this.setMaxStackSize(1);
        this.setMaxDamage(0);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void inputKey(InputEvent.KeyInputEvent e) {
        System.out.println(e);
        if(ExampleMod.key_S.isPressed())key[S] = true;
        else key[S] = false;
        if(ExampleMod.key_W.isPressed())key[W] = true;
        else key[W] = false;
        if(ExampleMod.key_A.isPressed())key[A] = true;
        else key[A] = false;
        if(ExampleMod.key_D.isPressed())key[D] = true;
        else key[D] = false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        mPlayer = playerIn;

        fly = true;
        if (timer == null) {
            timer = new Timer();
            TimerTask task = new TimerTask() {
                @SideOnly(Side.CLIENT)
                @Override
                public void run() {
                    if(Minecraft.getMinecraft().player != null) {
                        mPlayer = Minecraft.getMinecraft().player;
                        if(!mPlayer.isAirBorne)return;
                        if(!fly)return;
                        fly = !mPlayer.isCollided;
                        des = mPlayer.rayTrace(48,1.0F).hitVec;
                        double difX = des.x-mPlayer.posX;
                        double difY = des.y-mPlayer.posY;
                        double difZ = des.z-mPlayer.posZ;
                        double sum = Math.abs(difX)+Math.abs(difY)+Math.abs(difZ);
                        origin = new Vec3d(wireForce*(difX/sum),wireForce*(difY/sum),wireForce*(difZ/sum));
                        //No right click then fall
                        if(!Mouse.isButtonDown(1)) {
                            origin = acc = new Vec3d(0,4,0);
                            vx*=0.9;
                            vz*=0.9;
                            upCount++;
                            if (upCount >= 4){fly = false;}
                        }
                        else {
                            upCount = 0;
                            mPlayer.setVelocity(0,0,0);
                            if (Math.abs(mPlayer.posX-des.x)<=4){if(key[A]||key[D])vx *= 0.9;else vx *= 0.65;}
                            if (Math.abs(mPlayer.posY-des.y)<=4){if(key[W]||key[S])vy *= 0.9;else vy *= 0.65;}
                            if (Math.abs(mPlayer.posZ-des.z)<=4){if(key[A]||key[D])vz *= 0.9;else vz *= 0.65;}
                            acc = new Vec3d(
                                    key[A]&&!key[D]?origin.x+Math.cos(Math.toRadians(mPlayer.rotationYaw))*keyForce:!key[A]&&key[D]?origin.x-Math.cos(Math.toRadians(mPlayer.rotationYaw))*keyForce:origin.x,
                                    key[W]&&!key[S]?origin.y+keyForce/2:!key[W]&&key[S]?origin.y-keyForce/2:origin.y,
                                    key[A]&&!key[D]?origin.z+Math.sin(Math.toRadians(mPlayer.rotationYaw))*keyForce:!key[A]&&key[D]?origin.z-Math.sin(Math.toRadians(mPlayer.rotationYaw))*keyForce:origin.z
                            );
                        }
                        if(!fly) {
                            mPlayer.fall(-1,1);
                            vx = vy = vz = 0;
                            acc = Vec3d.ZERO;
                            return;
                        }
                        double ms = period/1000d;
                        vx += acc.x*ms;
                        vy += acc.y*ms;
                        vz += acc.z*ms;
                        mPlayer.setVelocity(vx,vy,vz);
                        //mPlayer.setPosition(mPlayer.posX +((destX-mPlayer.posX)/24f),mPlayer.posY+((destY-mPlayer.posY)/24f),mPlayer.posZ+((destZ-mPlayer.posZ)/24f));
                        mPlayer.setPosition(mPlayer.posX +vx*ms,mPlayer.posY+vy*ms,mPlayer.posZ+vz*ms);
                    }
                }
            };
            timer.schedule(task,300,period);
        }
        return super.onItemRightClick(worldIn,playerIn,handIn);
    }




}
