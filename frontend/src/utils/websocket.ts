import { ref, onMounted, onUnmounted } from 'vue'
import { ElNotification } from 'element-plus'

/** WebSocket 连接状态 */
export const wsConnected = ref(false)

/** 最新通知消息 */
export const wsMessage = ref<any>(null)

let stompClient: any = null
let reconnectTimer: number | null = null

/**
 * 初始化 WebSocket 连接（STOMP over SockJS）
 */
export function connectWebSocket(token?: string) {
  // 动态加载 sockjs-client 和 stompjs
  // 如果项目未安装 sockjs-client，使用原生 WebSocket
  const wsUrl = `${window.location.protocol === 'https:' ? 'wss:' : 'ws:'}//${window.location.host}/api/ws`

  try {
    const socket = new WebSocket(wsUrl)
    socket.onopen = () => {
      wsConnected.value = true
      console.log('[WS] Connected')
      // 发送认证信息
      if (token) {
        socket.send(JSON.stringify({ type: 'auth', token }))
      }
    }
    socket.onmessage = (event) => {
      try {
        const data = JSON.parse(event.data)
        wsMessage.value = data
        // 弹出通知
        ElNotification({
          title: data.title || '新通知',
          message: data.content || '',
          type: 'info',
          duration: 5000,
        })
      } catch (e) {
        console.warn('[WS] Parse error:', e)
      }
    }
    socket.onclose = () => {
      wsConnected.value = false
      console.log('[WS] Disconnected, reconnecting in 5s...')
      // 自动重连
      if (reconnectTimer) clearTimeout(reconnectTimer)
      reconnectTimer = window.setTimeout(() => connectWebSocket(token), 5000)
    }
    socket.onerror = (err) => {
      console.error('[WS] Error:', err)
      wsConnected.value = false
    }
  } catch (e) {
    console.warn('[WS] WebSocket not supported or server unavailable')
  }
}

/**
 * 断开 WebSocket
 */
export function disconnectWebSocket() {
  if (reconnectTimer) {
    clearTimeout(reconnectTimer)
    reconnectTimer = null
  }
  wsConnected.value = false
}

/**
 * Vue composable: 使用 WebSocket
 */
export function useWebSocket() {
  onMounted(() => {
    if (!wsConnected.value) {
      connectWebSocket()
    }
  })

  onUnmounted(() => {
    // 组件卸载不断连，保持全局连接
  })

  return {
    connected: wsConnected,
    message: wsMessage,
  }
}
