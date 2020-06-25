package mcvmcomputers.item;

import java.lang.reflect.Constructor;

import mcvmcomputers.client.ClientMod;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class PlacableOrderableItem extends OrderableItem{
	private Constructor<? extends Entity> constructor;
	private SoundEvent placeSound;
	public final boolean wallTV;
	
	public PlacableOrderableItem(Settings settings, Class<? extends Entity> entityPlaced, SoundEvent placeSound, int price, boolean wallTV) {
		super(settings, price);
		this.wallTV = wallTV;
		this.placeSound = placeSound;
		try {
			constructor = entityPlaced.getConstructor(World.class, Double.class, Double.class, Double.class, Vec3d.class, String.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public PlacableOrderableItem(Settings settings, Class<? extends Entity> entityPlaced, SoundEvent placeSound, int price) {
		this(settings, entityPlaced, placeSound, price, false);
	}
	
	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		if(!world.isClient && hand == Hand.MAIN_HAND) {
			user.getStackInHand(hand).decrement(1);
			HitResult hr = user.rayTrace(10, 0f, false);
			Entity ek;
			try {
				ek = constructor.newInstance(world, 
											hr.getPos().getX(),
											hr.getPos().getY(),
											hr.getPos().getZ(),
											new Vec3d(user.getPosVector().x,
														hr.getPos().getY(),
														user.getPosVector().z), user.getUuid().toString());
				world.spawnEntity(ek);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if(world.isClient) {
			world.playSound(ClientMod.thePreviewEntity.getX(),
							ClientMod.thePreviewEntity.getY(),
							ClientMod.thePreviewEntity.getZ(),
							placeSound,
							SoundCategory.BLOCKS, 1, 1, true);
		}
		
		return new TypedActionResult<ItemStack>(ActionResult.SUCCESS, user.getStackInHand(hand));
	}

}