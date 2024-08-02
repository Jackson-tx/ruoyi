import request from '@/utils/request'


// 查询缓存名称列表
export function listVisitCount() {
  return request({
    url: '/monitor/cache/visitCount',
    method: 'get'
  })
}

// 清理指定名称缓存
export function clearCacheName(cacheKey) {
  return request({
    url: '/monitor/cache/clearvisitCountKey' + cacheKey,
    method: 'delete'
  })
}


