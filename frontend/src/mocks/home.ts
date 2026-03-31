export const mockHomeData = {
  banners: [
    { id: 1, title: "新宠到家全攻略", image_url: "https://images.unsplash.com/photo-1450778869180-41d0601e046e?auto=format&fit=crop&w=800&q=80" },
    { id: 2, title: "春季驱虫特惠", image_url: "https://images.unsplash.com/photo-1548199973-03cce0bbc87b?auto=format&fit=crop&w=800&q=80" },
    { id: 3, title: "萌宠摄影大赛", image_url: "https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?auto=format&fit=crop&w=800&q=80" }
  ],
  quick_entries: [
    { code: "community", title: "宠物社区", path: "/community" },
    { code: "adoption", title: "领养中心", path: "/adoption" },
    { code: "services", title: "宠物服务", path: "/services" },
    { code: "shop", title: "宠物商城", path: "/shop" }
  ],
  recommended_posts: [
    {
      id: 1,
      title: "如何让猫咪适应新环境",
      author: { name: "喵星人", avatar: "https://api.dicebear.com/7.x/avataaars/svg?seed=1" },
      cover: "https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?auto=format&fit=crop&w=400&q=80",
      likes: 120,
      comments: 45
    },
    {
      id: 2,
      title: "狗狗行为解读：摇尾巴不一定开心",
      author: { name: "汪星人", avatar: "https://api.dicebear.com/7.x/avataaars/svg?seed=2" },
      cover: "https://images.unsplash.com/photo-1517849845537-4d257902454a?auto=format&fit=crop&w=400&q=80",
      likes: 85,
      comments: 23
    },
    {
      id: 3,
      title: "养兔新手必看：兔兔饮食指南",
      author: { name: "兔兔酱", avatar: "https://api.dicebear.com/7.x/avataaars/svg?seed=3" },
      cover: "https://images.unsplash.com/photo-1585110396067-49f6347160f9?auto=format&fit=crop&w=400&q=80",
      likes: 230,
      comments: 67
    }
  ],
  recommended_services: [
    {
      id: 1,
      name: "爪爪宠物美容",
      location: "朝阳区",
      rating: 4.9,
      image: "https://images.unsplash.com/photo-1516734212186-a967f81ad0d7?auto=format&fit=crop&w=400&q=80",
      tags: ["美容", "洗澡", "造型"]
    },
    {
      id: 2,
      name: "汪星人训犬学校",
      location: "海淀区",
      rating: 4.8,
      image: "https://images.unsplash.com/photo-1587300003388-59208cc962cb?auto=format&fit=crop&w=400&q=80",
      tags: ["训练", "寄养", "教育"]
    }
  ],
  recommended_products: [
    {
      id: 1,
      name: "天然无谷猫粮 10kg",
      price: 299,
      image: "https://images.unsplash.com/photo-1589924691995-400dc9ecc119?auto=format&fit=crop&w=400&q=80",
      sales: 500
    },
    {
      id: 2,
      name: "逗猫激光笔",
      price: 29.9,
      image: "https://images.unsplash.com/photo-1545249390-6bdfa286032f?auto=format&fit=crop&w=400&q=80",
      sales: 1200
    },
    {
      id: 3,
      name: "宠物自动饮水机",
      price: 159,
      image: "https://images.unsplash.com/photo-1583337130417-3346a1be7dee?auto=format&fit=crop&w=400&q=80",
      sales: 300
    }
  ],
  tips: [
    { title: "春季驱虫", content: "春天到了，记得给宠物做好体内外驱虫，预防跳蚤和蜱虫。" },
    { title: "换毛季", content: "春季是宠物换毛季，多给宠物梳理毛发，可以减少毛球症的发生。" },
    { title: "发情期", content: "春季宠物发情期到了，如果没有繁殖计划，建议尽早做绝育。" }
  ],
  pet_cards: [
    { title: "萌宠展示", subtitle: "分享你的可爱瞬间", image_url: "https://images.unsplash.com/photo-1519052537078-e6302a4968d4?auto=format&fit=crop&w=400&q=80" },
    { title: "可爱暴击", subtitle: "一键云吸宠物", image_url: "https://images.unsplash.com/photo-1537151625747-768eb6cf92b2?auto=format&fit=crop&w=400&q=80" }
  ]
};