package fnitcrafters.iqabc;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemFood;

public class Sample extends ItemFood{
    public Sample() {
        super(30,30,false);
        setCreativeTab(CreativeTabs.FOOD);
        setUnlocalizedName("FOOD");
        setMaxStackSize(64);
        setAlwaysEdible();
    }
}
