import request from '../request'
import type { ApiResponse, PageResult, PageQuery } from '@/types/api'
import type { DictType, DictData } from '@/types/system'

/** 查询字典类型列表 */
export function listDictType(params: PageQuery): Promise<ApiResponse<PageResult<DictType>>> {
  return request.get('/system/dict/type', { params })
}

/** 查询字典类型详情 */
export function getDictType(dictId: number): Promise<ApiResponse<DictType>> {
  return request.get(`/system/dict/type/${dictId}`)
}

/** 新增字典类型 */
export function addDictType(data: DictType): Promise<ApiResponse<void>> {
  return request.post('/system/dict/type', data)
}

/** 修改字典类型 */
export function updateDictType(data: DictType): Promise<ApiResponse<void>> {
  return request.put('/system/dict/type', data)
}

/** 删除字典类型 */
export function deleteDictType(dictIds: string): Promise<ApiResponse<void>> {
  return request.delete(`/system/dict/type/${dictIds}`)
}

/** 刷新字典缓存 */
export function refreshDictCache(): Promise<ApiResponse<void>> {
  return request.delete('/system/dict/type/refreshCache')
}

/** 查询字典数据列表 */
export function listDictData(params: { dictType: string }): Promise<ApiResponse<DictData[]>> {
  return request.get('/system/dict/data', { params })
}

/** 根据字典类型获取字典数据 */
export function getDictDataByType(dictType: string): Promise<ApiResponse<DictData[]>> {
  return request.get(`/system/dict/data/type/${dictType}`)
}

/** 新增字典数据 */
export function addDictData(data: DictData): Promise<ApiResponse<void>> {
  return request.post('/system/dict/data', data)
}

/** 修改字典数据 */
export function updateDictData(data: DictData): Promise<ApiResponse<void>> {
  return request.put('/system/dict/data', data)
}

/** 删除字典数据 */
export function deleteDictData(dictCodes: string): Promise<ApiResponse<void>> {
  return request.delete(`/system/dict/data/${dictCodes}`)
}
