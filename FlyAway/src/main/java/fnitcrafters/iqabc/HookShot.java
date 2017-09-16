package fnitcrafters.iqabc;

import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;

import java.util.Timer;
import java.util.TimerTask;

public class HookShot extends ItemBow {

        private EntityPlayer mPlayer;
        private final int period= 40;
        private double destX,destY,destZ;
        private double vx=0.1,vy=0.1,vz=0.1;//velocity
        private double ax=0.1,ay=0.1,az=0.1;//acceleration
        private int upCount = 0;
        private Timer timer = null;
        private boolean fly = false;

        public HookShot() {
            super();
            this.setCreativeTab(CreativeTabs.COMBAT);
            this.setUnlocalizedName("HookShot");
            this.setRegistryName("hookshot");
            this.setMaxStackSize(1);
            this.setMaxDamage(0);
        }

        @Override
        @SideOnly(Side.CLIENT)
        public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
            mPlayer = playerIn;
            Vec3d dest = mPlayer.rayTrace(48,1.0F).hitVec;
            final double power = 3;
            destX = dest.x;
            destY = dest.y;
            destZ = dest.z;
            double difX = destX-mPlayer.posX;
            double difY = destY-mPlayer.posY;
            double difZ = destZ-mPlayer.posZ;
            double sum = Math.abs(difX)+Math.abs(difY)+Math.abs(difZ);
            ax = power*(difX/sum);
            ay = power*(difY/sum);
            az = power*(difZ/sum);
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
                            if(!fly) {return;}
                            fly = !mPlayer.isCollided;
                            boolean isDown = Mouse.isButtonDown(1);
                            if(!isDown) {
                                //mPlayer.fall(-1,2);
                                ax = 0;
                                vx *= 0.9;
                                ay = 7;
                                az = 0;
                                vz *= 0.9;
                                upCount++;
                                if (upCount >= 4){fly = false;}
                            }
                            else {
                                upCount = 0;
                                mPlayer.setVelocity(0,0,0);
                                if (Math.abs(mPlayer.posX-destX)<=4){vx *= 0.6;}
                                if (Math.abs(mPlayer.posY-destY)<=4){vy *= 0.6;}
                                if (Math.abs(mPlayer.posZ-destZ)<=4){vz *= 0.6;}
                            }
                            if(!fly) {
                                mPlayer.fall(-1,1);
                                ax = vx = 0;
                                ay = vy = 0;
                                az = vz = 0;
                                return;
                            }
                            double ms = period/1000d;
                            vx += ax*ms;
                            vy += ay*ms;
                            vz += az*ms;
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
