import request from '../request'
import type { ApiResponse, PageResult, PageQuery } from '@/types/api'
import type { PostInfo } from '@/types/system'

/** 查询岗位列表 */
export function listPost(params: PageQuery): Promise<ApiResponse<PageResult<PostInfo>>> {
  return request.get('/system/post', { params })
}

/** 查询岗位详情 */
export function getPost(postId: number): Promise<ApiResponse<PostInfo>> {
  return request.get(`/system/post/${postId}`)
}

/** 新增岗位 */
export function addPost(data: PostInfo): Promise<ApiResponse<void>> {
  return request.post('/system/post', data)
}

/** 修改岗位 */
export function updatePost(data: PostInfo): Promise<ApiResponse<void>> {
  return request.put('/system/post', data)
}

/** 删除岗位 */
export function deletePost(postIds: string): Promise<ApiResponse<void>> {
  return request.delete(`/system/post/${postIds}`)
}

/** 获取岗位选项 */
export function getPostOptions(): Promise<ApiResponse<PostInfo[]>> {
  return request.get('/system/post/optionselect')
}
