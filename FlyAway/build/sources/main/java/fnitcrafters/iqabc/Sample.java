package fnitcrafters.iqabc;

import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;

import java.util.Timer;
import java.util.TimerTask;


public class Sample extends ItemBow{

    private EntityPlayer mPlayer;
    private final int period= 40;
    private double destX,destY,destZ;
    private double vx=0.1,vy=0.1,vz=0.1;//velocity
    private double ax=0.1,ay=0.1,az=0.1;//acceleration
    private int upCount = 0;
    private Timer timer = null;
    private boolean fly = false;

    public Sample() {
        super();
        setCreativeTab(CreativeTabs.COMBAT);
        setUnlocalizedName("HookShot");
        setMaxStackSize(1);
        setRegistryName("hookshot");
    }
    class aaa extends  ItemSword{
        public aaa(ToolMaterial toolMaterial){
            super(toolMaterial);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        mPlayer = playerIn;
        //RayTraceResult result = mPlayer.rayTrace(Minecraft.getMinecraft().objectMouseOver.,1.0F);
        //mPlayer.setPosition(playerIn.posX+1,playerIn.posY,playerIn.posZ);
        //BlockPos pos = Minecraft.getMinecraft().objectMouseOver.getBlockPos();
        //Vec3d dest = Minecraft.getMinecraft().objectMouseOver.hitVec;
        Vec3d dest = mPlayer.rayTrace(48,1.0F).hitVec;
        //System.out.println("dest1  =  " + dest);
        //mPlayer.rayTrace(50,1.0F);

        final double power = 3;
        destX = dest.x;
        destY = dest.y;
        destZ = dest.z;
        double difX = destX-mPlayer.posX;
        double difY = destY-mPlayer.posY;
        double difZ = destZ-mPlayer.posZ;
        double sum = Math.abs(difX)+Math.abs(difY)+Math.abs(difZ);
        //mPlayer.setPosition(destX,destY,destZ);
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
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        //boolean flag = !this.findAmmo(playerIn).isEmpty();
        ActionResult<ItemStack> ret = net.minecraftforge.event.ForgeEventFactory.onArrowNock(itemstack, worldIn, playerIn, handIn, true);
        if (ret != null) return ret;
        return new ActionResult(EnumActionResult.PASS,playerIn.getHeldItem(handIn));
    }

    /*
    private ItemStack findAmmo(EntityPlayer player) {
        if (this.isArrow(player.getHeldItem(EnumHand.OFF_HAND)))
        {
            return player.getHeldItem(EnumHand.OFF_HAND);
        }
        else if (this.isArrow(player.getHeldItem(EnumHand.MAIN_HAND)))
        {
            return player.getHeldItem(EnumHand.MAIN_HAND);
        }
        else
        {
            for (int i = 0; i < player.inventory.getSizeInventory(); ++i)
            {
                ItemStack itemstack = player.inventory.getStackInSlot(i);

                if (this.isArrow(itemstack))
                {
                    return itemstack;
                }
            }

            return ItemStack.EMPTY;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {
        timeLeft = 10;
        {
            if (entityLiving instanceof EntityPlayer)
            {
                EntityPlayer entityplayer = (EntityPlayer)entityLiving;
                boolean flag = entityplayer.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, stack) > 0;
                ItemStack itemstack = findAmmo(entityplayer);

                int i = this.getMaxItemUseDuration(stack) - timeLeft;
                i = net.minecraftforge.event.ForgeEventFactory.onArrowLoose(stack, worldIn, entityplayer, i, !itemstack.isEmpty() || flag);
                if (i < 0) return;

                if (!itemstack.isEmpty() || flag)
                {
                    if (itemstack.isEmpty())
                    {
                        itemstack = new ItemStack(Items.ARROW);
                    }

                    float f = getArrowVelocity(i);

                    if ((double)f >= 0.1D)
                    {
                        boolean flag1 = entityplayer.capabilities.isCreativeMode || (itemstack.getItem() instanceof ItemArrow && ((ItemArrow) itemstack.getItem()).isInfinite(itemstack, stack, entityplayer));

                        if (!worldIn.isRemote)
                        {
                            ItemArrow itemarrow = (ItemArrow)(itemstack.getItem() instanceof ItemArrow ? itemstack.getItem() : Items.ARROW);
                            EntityArrow entityarrow = itemarrow.createArrow(worldIn, itemstack, entityplayer);
                            entityarrow.setAim(entityplayer, entityplayer.rotationPitch, entityplayer.rotationYaw, 0.0F, f * 3.0F*10, 1.0F);
                            entityarrow.setVelocity(entityarrow.motionX*100,entityarrow.motionY*100,entityarrow.motionZ*100);

                            if (f == 1.0F)
                            {
                                entityarrow.setIsCritical(true);
                            }

                            int j = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, stack);

                            if (j > 0)
                            {
                                entityarrow.setDamage(entityarrow.getDamage() + (double)j * 0.5D + 0.5D);
                            }

                            int k = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, stack);

                            if (k > 0)
                            {
                                entityarrow.setKnockbackStrength(k);
                            }

                            if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, stack) > 0)
                            {
                                entityarrow.setFire(100);
                            }

                            stack.damageItem(1, entityplayer);

                            if (flag1 || entityplayer.capabilities.isCreativeMode && (itemstack.getItem() == Items.SPECTRAL_ARROW || itemstack.getItem() == Items.TIPPED_ARROW))
                            {
                                entityarrow.pickupStatus = EntityArrow.PickupStatus.CREATIVE_ONLY;
                            }

                            worldIn.spawnEntity(entityarrow);
                        }

                        worldIn.playSound((EntityPlayer)null, entityplayer.posX, entityplayer.posY, entityplayer.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + f * 0.5F);

                        if (!flag1 && !entityplayer.capabilities.isCreativeMode)
                        {
                            itemstack.shrink(1);

                            if (itemstack.isEmpty())
                            {
                                entityplayer.inventory.deleteStack(itemstack);
                            }
                        }

                        entityplayer.addStat(StatList.getObjectUseStats(this));
                    }
                }
            }
        }
        super.onPlayerStoppedUsing(stack, worldIn, entityLiving, timeLeft);
    }
*/}
