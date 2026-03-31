export const mockServiceAddons = [
  // 免费基础护理
  { id: "f1", name: "修剪指甲", price: 0, group: "免费基础" },
  { id: "f2", name: "清理耳道", price: 0, group: "免费基础" },
  { id: "f3", name: "修剪脚底毛", price: 0, group: "免费基础" },
  { id: "f4", name: "挤肛门腺", price: 0, group: "免费基础" },
  { id: "f5", name: "剃腹底毛", price: 0, group: "免费基础" },
  // 局部护理
  { id: "p1", name: "眼睛清洁", price: 15, group: "局部护理" },
  { id: "p2", name: "牙齿清洁", price: 30, group: "局部护理" },
  { id: "p3", name: "面部美容", price: 20, group: "局部护理" },
  { id: "p4", name: "耳朵护理", price: 25, group: "局部护理" },
  { id: "p5", name: "足部护理", price: 20, group: "局部护理" },
  { id: "p6", name: "尾部造型", price: 15, group: "局部护理" },
  // 精洗与被毛护理
  { id: "c1", name: "深层去死毛", price: 40, group: "被毛护理" },
  { id: "c2", name: "蓬松吹风", price: 30, group: "被毛护理" },
  { id: "c3", name: "护毛素护理", price: 35, group: "被毛护理" },
  { id: "c4", name: "止毛结护理", price: 50, group: "被毛护理" },
  { id: "c5", name: "保湿SPA", price: 60, group: "被毛护理" },
  // 高级护理
  { id: "h1", name: "精油按摩", price: 50, group: "高级护理" },
  { id: "h2", name: "芳香SPA", price: 80, group: "高级护理" },
  { id: "h3", name: "泥膜护理", price: 60, group: "高级护理" },
  { id: "h4", name: "草本药浴", price: 70, group: "高级护理" },
  { id: "h5", name: "美毛护肤素", price: 40, group: "高级护理" },
  { id: "h6", name: "驱虫药浴", price: 90, group: "高级护理" }
];

export const mockMerchants = [
  {
    id: 1,
    name: "爪爪宠物美容",
    category: "宠物美容",
    district: "朝阳区",
    address: "建国路88号SOHO现代城",
    phone: "010-12345678",
    business_hours: "09:00-21:00",
    status: "营业中",
    rating: 4.9,
    cover_url: "https://images.unsplash.com/photo-1516734212186-a967f81ad0d7?auto=format&fit=crop&w=600&q=80",
    description: "专业宠物美容 SPA，提供洗澡、造型、驱虫等全方位服务。",
    services: [
      { id: 101, name: "基础洗浴", price: 88, duration: "45分钟" },
      { id: 102, name: "精致洗护", price: 168, duration: "75分钟" },
      { id: 103, name: "全身造型", price: 288, duration: "120分钟" },
      { id: 104, name: "会员专属", price: 368, duration: "150分钟" }
    ]
  },
  {
    id: 2,
    name: "汪星人训犬学校",
    category: "宠物训练",
    district: "海淀区",
    address: "中关村大街1号",
    phone: "010-87654321",
    business_hours: "08:00-20:00",
    status: "营业中",
    rating: 4.8,
    cover_url: "https://images.unsplash.com/photo-1587300003388-59208cc962cb?auto=format&fit=crop&w=600&q=80",
    description: "专业训犬师团队，提供基础训练、行为纠正、技能提升等课程。",
    services: [
      { id: 201, name: "幼犬托管", price: 150, duration: "1天" },
      { id: 202, name: "行为纠正", price: 2000, duration: "10次课" },
      { id: 203, name: "技能训练", price: 3000, duration: "20次课" }
    ]
  },
  {
    id: 3,
    name: "萌宠宠物医院",
    category: "宠物医院",
    district: "西城区",
    address: "西单北大街120号",
    phone: "010-55667788",
    business_hours: "24小时",
    status: "营业中",
    rating: 4.7,
    cover_url: "https://images.unsplash.com/photo-1629909613654-28e377c37b09?auto=format&fit=crop&w=600&q=80",
    description: "专业宠物医疗团队，擅长内科、外科、疫苗接种。",
    services: [
      { id: 301, name: "疫苗接种", price: 80, duration: "30分钟" },
      { id: 302, name: "体检套餐", price: 299, duration: "60分钟" },
      { id: 303, name: "驱虫服务", price: 100, duration: "30分钟" }
    ]
  },
  {
    id: 4,
    name: "猫咪咖啡馆",
    category: "宠物寄养",
    district: "东城区",
    address: "南锣鼓巷15号",
    phone: "010-99887766",
    business_hours: "10:00-22:00",
    status: "营业中",
    rating: 4.6,
    cover_url: "https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?auto=format&fit=crop&w=600&q=80",
    description: "撸猫圣地，提供饮品和小食，享受悠闲下午时光。",
    services: [
      { id: 401, name: "单人套餐", price: 58, duration: "2小时" },
      { id: 402, name: "双人套餐", price: 98, duration: "2小时" }
    ]
  }
];

/** 店铺优惠套餐（多服务组合价） */
export const mockServicePackages = [
  // 爪爪宠物美容
  {
    id: "p1-basic",
    merchantId: 1,
    name: "基础洗护套餐",
    serviceIds: [101, 103],
    price: 128
  },
  {
    id: "p1-premium",
    merchantId: 1,
    name: "精致SPA套餐",
    serviceIds: [102, 103],
    price: 398
  },
  {
    id: "p1-vip",
    merchantId: 1,
    name: "会员尊享套餐",
    serviceIds: [102, 104],
    price: 468
  },
  // 训犬学校
  {
    id: "p2-boarding",
    merchantId: 2,
    name: "托管月卡套餐",
    serviceIds: [201, 201, 201, 201, 201],
    price: 680
  }
];

export function getMockPackagesByMerchantId(merchantId: number) {
  return mockServicePackages.filter((p) => p.merchantId === merchantId);
}

export function getMockMerchantById(id: number) {
  return mockMerchants.find((m) => m.id === id) ?? null;
}
