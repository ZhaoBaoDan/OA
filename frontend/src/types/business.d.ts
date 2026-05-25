/** 流程定义 */
export interface ProcessDef {
  id: number
  processKey: string
  processName: string
  version: number
  category: string
  bpmnXml: string
  description: string
  status: number
  createTime?: string
}

/** 流程实例 */
export interface ProcessInst {
  id: number
  processDefId: number
  processKey: string
  processName: string
  businessKey: string
  startUserId: number
  startUserName: string
  status: string
  startTime: string
  endTime: string
  duration: number
  variables: string
}

/** 审批记录 */
export interface ApprovalRecord {
  id: number
  processInstId: number
  nodeId: string
  nodeName: string
  nodeType: string
  assigneeId: number
  assigneeName: string
  action: string
  comment: string
  status: string
  startTime: string
  endTime: string
  createTime: string
}

/** 任务 */
export interface TaskItem {
  id: number
  title: string
  description: string
  priority: string
  status: string
  assigneeId: number
  assignee: string
  dueDate: string
  tags: string
  sort: number
  createTime?: string
}

/** 考勤记录 */
export interface AttendanceRecord {
  id: number
  userId: number
  userName: string
  checkDate: string
  checkIn: string
  checkOut: string
  status: string
  workHours: number
  remark: string
}

/** 文档 */
export interface DocumentItem {
  id: number
  fileName: string
  fileType: string
  fileSize: number
  fileUrl: string
  uploadBy: number
  uploadName: string
  createTime: string
}

/** 会议室 */
export interface MeetingRoom {
  id: number
  name: string
  capacity: number
  equipment: string
  location: string
  status: number
}

/** 会议室预约 */
export interface MeetingBooking {
  id: number
  roomId: number
  bookingDate: string
  startTime: string
  endTime: string
  title: string
  organizerId: number
  organizer: string
  attendees: string
  remark: string
}

/** 通知 */
export interface NotificationItem {
  id: number
  title: string
  content: string
  type: string
  userId: number
  isRead: number
  readTime: string
  createTime: string
}

/** 看板数据 */
export interface BoardData {
  todo: TaskItem[]
  progress: TaskItem[]
  done: TaskItem[]
  archive: TaskItem[]
}

/** 月度考勤统计 */
export interface AttendanceStats {
  totalDays: number
  normalDays: number
  lateCount: number
  earlyLeaveCount: number
  absentDays?: number
  leaveDays?: number
  overtimeHours?: number
}

/** 报表仪表盘数据 */
export interface DashboardData {
  attendance: AttendanceStats
  taskBoard: BoardData
}
