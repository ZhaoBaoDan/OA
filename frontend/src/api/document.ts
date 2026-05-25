import request from '@/api/request'

/** 文档列表 */
export function listDocument(params: any) {
  return request.get('/document', { params })
}

/** 文档详情 */
export function getDocument(id: number) {
  return request.get(`/document/${id}`)
}

/** 上传文档 */
export function uploadDocument(file: File, folderId?: number, userId?: number, userName?: string) {
  const formData = new FormData()
  formData.append('file', file)
  if (folderId) formData.append('folderId', String(folderId))
  if (userId) formData.append('userId', String(userId))
  if (userName) formData.append('userName', userName)
  return request.post('/document/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  })
}

/** 删除文档 */
export function deleteDocument(id: number) {
  return request.delete(`/document/${id}`)
}

/** 下载文档 */
export function downloadDocument(id: number) {
  return request.get(`/document/${id}/download`)
}

/** 预览文档 */
export function previewDocument(id: number) {
  return request.get(`/document/${id}/preview`)
}

/** 创建版本 */
export function createVersion(id: number, file: File, userId: number, userName: string, remark?: string) {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('userId', String(userId))
  formData.append('userName', userName)
  if (remark) formData.append('remark', remark)
  return request.post(`/document/${id}/version`, formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  })
}

/** 版本列表 */
export function getVersions(id: number) {
  return request.get(`/document/${id}/versions`)
}

/** 全文检索 */
export function searchDocument(data: { keyword: string; pageNum?: number; pageSize?: number }) {
  return request.post('/document/search', data)
}

/** 文件夹树 */
export function getFolderTree() {
  return request.get('/document/folder/tree')
}

/** 创建文件夹 */
export function createFolder(data: { name: string; parentId?: number; creatorId: number }) {
  return request.post('/document/folder', data)
}

/** 删除文件夹 */
export function deleteFolder(id: number) {
  return request.delete(`/document/folder/${id}`)
}

// ========== 全文检索 ==========

/** 全文搜索文档 */
export function searchDocument(keyword: string) {
  return request.get('/document/search', { params: { keyword } })
}

/** 按类型搜索 */
export function searchByType(fileType: string) {
  return request.get('/document/search/type', { params: { fileType } })
}
