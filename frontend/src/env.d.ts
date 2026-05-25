/// <reference types="vite/client" />

declare module '*.vue' {
  import type { DefineComponent } from 'vue'
  const component: DefineComponent<{}, {}, any>
  export default component
}

declare module 'bpmn-js/lib/Modeler' {
  const BpmnModeler: any
  export default BpmnModeler
}

declare module 'nprogress' {
  const NProgress: {
    start: () => void
    done: () => void
    configure: (options: { showSpinner?: boolean }) => void
  }
  export default NProgress
}

interface ImportMetaEnv {
  readonly VITE_APP_TITLE: string
  readonly VITE_APP_BASE_API: string
  readonly VITE_APP_ENV: string
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}
