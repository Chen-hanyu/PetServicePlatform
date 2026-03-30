export const mockMerchants = [
  {
    id: 1,
    name: "爪爪宠物美容",
    district: "朝阳区",
    address: "建国路88号SOHO现代城",
    phone: "010-12345678",
    business_hours: "09:00-21:00",
    status: "营业中",
    rating: 4.9,
    cover_url: "https://images.unsplash.com/photo-1516734212186-a967f81ad0d7?auto=format&fit=crop&w=600&q=80",
    description: "专业宠物美容 SPA，提供洗澡、造型、修剪等服务。",
    services: [
      { id: 101, name: "精致洗护", price: 128, duration: "60分钟" },
      { id: 102, name: "全身造型", price: 288, duration: "120分钟" },
      { id: 103, name: "美甲护理", price: 58, duration: "30分钟" }
    ]
  },
  {
    id: 2,
    name: "汪星人训犬学校",
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