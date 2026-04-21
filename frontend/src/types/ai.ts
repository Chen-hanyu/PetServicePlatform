export interface AIMessage {
  id: string;
  role: "user" | "assistant";
  content: string;
  timestamp: number;
}

export interface AIChatRequest {
  message: string;
  context?: string;
}

export interface AIChatResponse {
  reply: string;
  suggestions?: string[];
}

export interface AISuggestion {
  id: string;
  icon: string;
  title: string;
  description: string;
  placeholder: string;
}

export const AI_SUGGESTIONS: AISuggestion[] = [
  {
    id: "health",
    icon: "🩺",
    title: "宠物健康咨询",
    description: "咨询宠物疾病症状",
    placeholder: "描述一下宠物最近的症状..."
  },
  {
    id: "habit",
    icon: "🐾",
    title: "宠物习性问答",
    description: "了解宠物行为习惯",
    placeholder: "我家猫总是半夜叫是什么原因..."
  },
  {
    id: "diet",
    icon: "🍖",
    title: "宠物饮食建议",
    description: "咨询喂养知识",
    placeholder: "3个月的小狗适合吃什么..."
  },
  {
    id: "care",
    icon: "✨",
    title: "日常护理技巧",
    description: "美容清洁护理",
    placeholder: "如何给猫咪清理耳螨..."
  }
];
