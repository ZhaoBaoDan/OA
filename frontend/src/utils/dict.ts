import { ref } from 'vue'

// 字典缓存
const dictCache = new Map<string, any[]>()

/**
 * 获取字典数据
 */
export async function getDictData(dictType: string): Promise<any[]> {
  if (dictCache.has(dictType)) {
    return dictCache.get(dictType)!
  }
  try {
    const { getDictDataByType } = await import('@/api/system/dict')
    const res = await getDictDataByType(dictType)
    const data = res.data || []
    dictCache.set(dictType, data)
    return data
  } catch {
    return []
  }
}

/**
 * 获取字典标签
 */
export function getDictLabel(dictList: any[], value: string | number): string {
  const item = dictList.find((d) => d.dictValue === value)
  return item ? item.dictLabel : String(value)
}

/**
 * 获取字典标签样式
 */
export function getDictTagType(dictList: any[], value: string | number): string {
  const item = dictList.find((d) => d.dictValue === value)
  return item?.cssClass || ''
}

/**
 * 清除字典缓存
 */
export function clearDictCache(dictType?: string): void {
  if (dictType) {
    dictCache.delete(dictType)
  } else {
    dictCache.clear()
  }
}

/**
 * 使用字典的 composable
 */
export function useDict(...dictTypes: string[]) {
  const dictMap = ref<Record<string, any[]>>({})

  const loadDict = async () => {
    const promises = dictTypes.map(async (type) => {
      const data = await getDictData(type)
      dictMap.value[type] = data
    })
    await Promise.all(promises)
  }

  loadDict()

  return { dictMap, loadDict }
}
