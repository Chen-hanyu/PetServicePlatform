import { defineStore } from "pinia";
import { ref, computed } from "vue";

export interface ShopCartLine {
  id: number;
  name: string;
  price: number;
  quantity: number;
  image: string;
}

export const useShopCartStore = defineStore("shopCart", () => {
  const items = ref<ShopCartLine[]>([]);

  const totalCount = computed(() => items.value.reduce((s, i) => s + i.quantity, 0));
  const totalAmount = computed(() => items.value.reduce((s, i) => s + i.price * i.quantity, 0));

  const add = (
    product: { id: number; name: string; price: number; image_url?: string; image?: string },
    quantity = 1
  ) => {
    const img = product.image_url || product.image || "";
    const line = items.value.find((x) => x.id === product.id);
    if (line) line.quantity += quantity;
    else items.value.push({ id: product.id, name: product.name, price: product.price, quantity, image: img });
  };

  const increaseQty = (id: number) => {
    const line = items.value.find((x) => x.id === id);
    if (line) line.quantity++;
  };

  const decreaseQty = (id: number) => {
    const line = items.value.find((x) => x.id === id);
    if (!line) return;
    if (line.quantity > 1) line.quantity--;
    else removeItem(id);
  };

  const removeItem = (id: number) => {
    items.value = items.value.filter((x) => x.id !== id);
  };

  const clearCart = () => {
    items.value = [];
  };

  return { items, totalCount, totalAmount, add, increaseQty, decreaseQty, removeItem, clearCart };
});
