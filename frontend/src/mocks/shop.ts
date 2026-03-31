export const mockProducts = [
  {
    id: 1,
    name: "天然无谷猫粮 10kg",
    subtitle: "无谷配方，增强免疫力",
    price: 299,
    original_price: 399,
    image_url: "https://images.unsplash.com/photo-1589924691995-400dc9ecc119?auto=format&fit=crop&w=400&q=80",
    category: "主粮",
    stock: 50,
    sales: 500,
    description:
      "甄选优质肉类与蔬果，无谷低敏配方，富含 Omega-3 与多种维生素，适合全龄猫咪长期食用。颗粒适中易咀嚼，帮助维持理想体态与毛发亮泽。",
    images: [
      "https://images.unsplash.com/photo-1589924691995-400dc9ecc119?auto=format&fit=crop&w=800&q=80",
      "https://images.unsplash.com/photo-1574158622682-e40e69881006?auto=format&fit=crop&w=800&q=80"
    ]
  },
  {
    id: 2,
    name: "逗猫激光笔",
    subtitle: "自动逗猫，省心省力",
    price: 29.9,
    original_price: 49,
    image_url: "https://images.unsplash.com/photo-1545249390-6bdfa286032f?auto=format&fit=crop&w=400&q=80",
    category: "玩具",
    stock: 100,
    sales: 1200,
    description: "一键开启自动逗猫模式，红光点吸引猫咪追逐，释放精力、减少拆家。轻巧便携，USB 充电，安全不伤眼（演示文案）。",
    images: [
      "https://images.unsplash.com/photo-1545249390-6bdfa286032f?auto=format&fit=crop&w=800&q=80",
      "https://images.unsplash.com/photo-1514888287514-7bf32017b9c0?auto=format&fit=crop&w=800&q=80"
    ]
  },
  {
    id: 3,
    name: "宠物自动饮水机",
    subtitle: "循环活水，保持水质新鲜",
    price: 159,
    original_price: 199,
    image_url: "https://images.unsplash.com/photo-1583337130417-3346a1be7dee?auto=format&fit=crop&w=400&q=80",
    category: "用品",
    stock: 30,
    sales: 300,
    description: "静音循环过滤，模拟活泉流动，鼓励宠物多饮水。可拆洗结构，滤芯可更换，适合猫犬共用。",
    images: [
      "https://images.unsplash.com/photo-1583337130417-3346a1be7dee?auto=format&fit=crop&w=800&q=80",
      "https://images.unsplash.com/photo-1601758228041-f3b2795255f1?auto=format&fit=crop&w=800&q=80"
    ]
  },
  {
    id: 4,
    name: "天然狗罐头 12罐装",
    subtitle: "营养美味，狗狗最爱",
    price: 89,
    original_price: 120,
    image_url: "https://images.unsplash.com/photo-1568640347023-a616a30bc3bd?auto=format&fit=crop&w=400&q=80",
    category: "零食",
    stock: 80,
    sales: 850,
    description: "真材实料大肉块，不含人工诱食剂。汤汁浓郁可拌粮，12 罐组合装更实惠，开罐即食。",
    images: [
      "https://images.unsplash.com/photo-1568640347023-a616a30bc3bd?auto=format&fit=crop&w=800&q=80",
      "https://images.unsplash.com/photo-1587300003388-59208cc962cb?auto=format&fit=crop&w=800&q=80"
    ]
  },
  {
    id: 5,
    name: "猫爬架大型",
    subtitle: "多层设计，释放猫咪天性",
    price: 399,
    original_price: 599,
    image_url: "https://images.unsplash.com/photo-1514888287514-7bf32017b9c0?auto=format&fit=crop&w=400&q=80",
    category: "玩具",
    stock: 20,
    sales: 150,
    description: "加粗剑麻柱耐抓耐磨，多层跳台与窝垫，满足攀爬、休息、磨爪需求。稳固底座防倾倒，适合多猫家庭。",
    images: [
      "https://images.unsplash.com/photo-1514888287514-7bf32017b9c0?auto=format&fit=crop&w=800&q=80",
      "https://images.unsplash.com/photo-1545249390-6bdfa286032f?auto=format&fit=crop&w=800&q=80"
    ]
  },
  {
    id: 6,
    name: "宠物外出背包",
    subtitle: "透气舒适，时尚便携",
    price: 129,
    original_price: 169,
    image_url: "https://images.unsplash.com/photo-1519098901909-b1553a1190af?auto=format&fit=crop&w=400&q=80",
    category: "用品",
    stock: 45,
    sales: 420,
    description: "三面透气网窗，内置安全扣防挣脱。肩带减压设计，适合短途出行与就医。多色可选（演示）。",
    images: [
      "https://images.unsplash.com/photo-1519098901909-b1553a1190af?auto=format&fit=crop&w=800&q=80",
      "https://images.unsplash.com/photo-1548199973-03cce0bbc87b?auto=format&fit=crop&w=800&q=80"
    ]
  }
];

export function getMockProductById(id: number) {
  const p = mockProducts.find((x) => x.id === id);
  if (!p) return null;
  return {
    ...p,
    images: p.images?.length ? p.images : [p.image_url]
  };
}
