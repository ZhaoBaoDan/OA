import request from '@/api/request'

// ========== 资产管理 ==========

/** 资产列表 */
export function listAsset(params: any) {
  return request.get('/asset', { params })
}

/** 资产详情 */
export function getAsset(id: number) {
  return request.get(`/asset/${id}`)
}

/** 新增资产 */
export function createAsset(data: any) {
  return request.post('/asset', data)
}

/** 修改资产 */
export function updateAsset(data: any) {
  return request.put('/asset', data)
}

/** 删除资产 */
export function deleteAsset(id: number) {
  return request.delete(`/asset/${id}`)
}

/** 资产领用 */
export function assignAsset(id: number, userId: number, userName: string) {
  return request.post(`/asset/${id}/assign`, null, { params: { userId, userName } })
}

/** 资产归还 */
export function returnAsset(id: number) {
  return request.post(`/asset/${id}/return`)
}

/** 资产报废 */
export function scrapAsset(id: number) {
  return request.post(`/asset/${id}/scrap`)
}

/** 资产统计 */
export function getAssetStatistics() {
  return request.get('/asset/statistics')
}

/** 资产流转记录 */
export function getAssetRecords(assetId: number) {
  return request.get('/asset/record', { params: { assetId } })
}

// ========== 资产分类 ==========

/** 分类树形列表 */
export function getCategoryTree() {
  return request.get('/asset/category/tree')
}

/** 分类详情 */
export function getCategory(id: number) {
  return request.get(`/asset/category/${id}`)
}

/** 新增分类 */
export function createCategory(data: any) {
  return request.post('/asset/category', data)
}

/** 修改分类 */
export function updateCategory(data: any) {
  return request.put('/asset/category', data)
}

/** 删除分类 */
export function deleteCategory(id: number) {
  return request.delete(`/asset/category/${id}`)
}
