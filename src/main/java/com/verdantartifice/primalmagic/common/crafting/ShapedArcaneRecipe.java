package com.verdantartifice.primalmagic.common.crafting;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.verdantartifice.primalmagic.common.research.SimpleResearchKey;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.IShapedRecipe;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class ShapedArcaneRecipe implements IArcaneRecipe, IShapedRecipe<CraftingInventory> {
    protected final int recipeWidth;
    protected final int recipeHeight;
    protected final NonNullList<Ingredient> recipeItems;
    protected final ItemStack recipeOutput;
    protected final ResourceLocation id;
    protected final String group;
    protected final SimpleResearchKey research;
    
    public ShapedArcaneRecipe(ResourceLocation id, String group, SimpleResearchKey research, int width, int height, NonNullList<Ingredient> items, ItemStack output) {
        this.id = id;
        this.group = group;
        this.research = research;
        this.recipeWidth = width;
        this.recipeHeight = height;
        this.recipeItems = items;
        this.recipeOutput = output;
    }

    @Override
    public boolean matches(CraftingInventory inv, World worldIn) {
        for (int i = 0; i <= inv.getWidth() - this.recipeWidth; i++) {
            for (int j = 0; j <= inv.getHeight() - this.recipeHeight; j++) {
                if (this.checkMatch(inv, i, j, true)) {
                    return true;
                }
                if (this.checkMatch(inv, i, j, false)) {
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean checkMatch(CraftingInventory inv, int widthOffset, int heightOffset, boolean flag) {
        for (int i = 0; i < inv.getWidth(); i++) {
            for (int j = 0; j < inv.getHeight(); j++) {
                int k = i - widthOffset;
                int l = j - heightOffset;
                Ingredient ingredient = Ingredient.EMPTY;
                if (k >= 0 && l >= 0 && k < this.recipeWidth && l < this.recipeHeight) {
                    if (flag) {
                        ingredient = this.recipeItems.get(this.recipeWidth - k - 1 + l * this.recipeWidth);
                    } else {
                        ingredient = this.recipeItems.get(k + l * this.recipeWidth);
                    }
                }
                if (!ingredient.test(inv.getStackInSlot(i + j * inv.getWidth()))) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public ItemStack getCraftingResult(CraftingInventory inv) {
        return this.recipeOutput.copy();
    }

    @Override
    public boolean canFit(int width, int height) {
        return width >= this.recipeWidth && height >= this.recipeHeight;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return this.recipeOutput;
    }
    
    @Override
    public NonNullList<Ingredient> getIngredients() {
        return this.recipeItems;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return RecipeSerializersPM.ARCANE_CRAFTING_SHAPED;
    }

    @Override
    public SimpleResearchKey getRequiredResearch() {
        return this.research;
    }

    @Override
    public int getRecipeWidth() {
        return this.recipeWidth;
    }

    @Override
    public int getRecipeHeight() {
        return this.recipeHeight;
    }

    protected static Map<String, Ingredient> deserializeKey(JsonObject jsonObject) {
        Map<String, Ingredient> map = new HashMap<>();
        for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            if (entry.getKey().length() != 1) {
                throw new JsonSyntaxException("Invalid key entry: '" + (String)entry.getKey() + "' is an invalid symbol (must be 1 character only).");
            }
            if (" ".equals(entry.getKey())) {
                throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");
            }
            map.put(entry.getKey(), Ingredient.deserialize(entry.getValue()));
        }
        map.put(" ", Ingredient.EMPTY);
        return map;
    }

    protected static NonNullList<Ingredient> deserializeIngredients(String[] pattern, Map<String, Ingredient> keys, int width, int height) {
        NonNullList<Ingredient> list = NonNullList.withSize(width * height, Ingredient.EMPTY);
        Set<String> keySet = new HashSet<>(keys.keySet());
        keySet.remove(" ");
        
        for (int i = 0; i < pattern.length; i++) {
            for (int j = 0; j < pattern[0].length(); j++) {
                String s = pattern[i].substring(j, j + 1);
                Ingredient ingredient = keys.get(s);
                if (ingredient == null) {
                    throw new JsonSyntaxException("Pattern references symbol '" + s + "' but it's not defined in the key");
                }
                keySet.remove(s);
                list.set(j + width * i, ingredient);
            }
        }
        
        if (!keySet.isEmpty()) {
            throw new JsonSyntaxException("Key defines symbols that aren't used in pattern: " + keySet);
        } else {
            return list;
        }
    }

    protected static String[] shrink(String... toShrink) {
        int i = Integer.MAX_VALUE;
        int j = 0;
        int k = 0;
        int l = 0;
        
        for (int i1 = 0; i1 < toShrink.length; ++i1) {
            String s = toShrink[i1];
            i = Math.min(i, firstNonSpace(s));
            int j1 = lastNonSpace(s);
            j = Math.max(j, j1);
            if (j1 < 0) {
                if (k == i1) {
                    ++k;
                }
                ++l;
            } else {
                l = 0;
            }
        }
        if (toShrink.length == l) {
            return new String[0];
        } else {
            String[] astring = new String[toShrink.length - l - k];
            for (int k1 = 0; k1 < astring.length; ++k1) {
               astring[k1] = toShrink[k1 + k].substring(i, j + 1);
            }
            return astring;
        }
    }

    private static int firstNonSpace(String str) {
        int i;
        for (i = 0; i < str.length() && str.charAt(i) == ' '; ++i) {}
        return i;
    }

    private static int lastNonSpace(String str) {
        int i;
        for (i = str.length() - 1; i >= 0 && str.charAt(i) == ' '; --i) {}
        return i;
    }

    protected static String[] patternFromJson(JsonArray jsonArray) {
        String[] astring = new String[jsonArray.size()];
        if (astring.length > 3) {
            throw new JsonSyntaxException("Invalid pattern: too many rows, 3 is maximum");
        } else if (astring.length == 0) {
            throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");
        } else {
            for(int i = 0; i < astring.length; ++i) {
                String s = JSONUtils.getString(jsonArray.get(i), "pattern[" + i + "]");
                if (s.length() > 3) {
                    throw new JsonSyntaxException("Invalid pattern: too many columns, 3 is maximum");
                }
                if (i > 0 && astring[0].length() != s.length()) {
                    throw new JsonSyntaxException("Invalid pattern: each row must be the same width");
                }
                astring[i] = s;
            }
            return astring;
        }
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<ShapedArcaneRecipe> {
        @Override
        public ShapedArcaneRecipe read(ResourceLocation recipeId, JsonObject json) {
            String group = JSONUtils.getString(json, "group", "");
            SimpleResearchKey research = SimpleResearchKey.parse(JSONUtils.getString(json, "research", ""));
            Map<String, Ingredient> map = ShapedArcaneRecipe.deserializeKey(JSONUtils.getJsonObject(json, "key"));
            String[] patternStrs = ShapedArcaneRecipe.shrink(ShapedArcaneRecipe.patternFromJson(JSONUtils.getJsonArray(json, "pattern")));
            int width = patternStrs[0].length();
            int height = patternStrs.length;
            NonNullList<Ingredient> ingredients = ShapedArcaneRecipe.deserializeIngredients(patternStrs, map, width, height);
            ItemStack result = ShapedRecipe.deserializeItem(JSONUtils.getJsonObject(json, "result"));
            return new ShapedArcaneRecipe(recipeId, group, research, width, height, ingredients, result);
        }

        @Override
        public ShapedArcaneRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
            int width = buffer.readVarInt();
            int height = buffer.readVarInt();
            String group = buffer.readString(32767);
            SimpleResearchKey research = SimpleResearchKey.parse(buffer.readString(32767));
            NonNullList<Ingredient> list = NonNullList.withSize(width * height, Ingredient.EMPTY);
            for (int index = 0; index < list.size(); index++) {
                list.set(index, Ingredient.read(buffer));
            }
            ItemStack stack = buffer.readItemStack();
            return new ShapedArcaneRecipe(recipeId, group, research, width, height, list, stack);
        }

        @Override
        public void write(PacketBuffer buffer, ShapedArcaneRecipe recipe) {
            buffer.writeVarInt(recipe.recipeWidth);
            buffer.writeVarInt(recipe.recipeHeight);
            buffer.writeString(recipe.group);
            buffer.writeString(recipe.research.toString());
            for (Ingredient ingredient : recipe.recipeItems) {
                ingredient.write(buffer);
            }
            buffer.writeItemStack(recipe.recipeOutput);
        }
    }
}
