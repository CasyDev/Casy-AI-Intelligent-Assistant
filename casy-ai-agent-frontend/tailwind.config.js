/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{vue,js,ts,jsx,tsx}"
  ],
  theme: {
    extend: {
      colors: {
        kimi: {
          bg: '#1a1a2e',
          sidebar: '#16162a',
          card: '#252542',
          border: '#2d2d4a',
          primary: '#6c5ce7',
          'primary-hover': '#5b4cdb',
          text: '#e4e4f0',
          'text-secondary': '#9ca3af',
          'text-muted': '#6b7280',
          code: '#2d2b55',
          'user-msg': '#6c5ce7',
          'ai-msg': '#252542',
          tool: '#10b981',
          think: '#f59e0b',
          step: '#3b82f6'
        }
      },
      fontFamily: {
        mono: ['JetBrains Mono', 'Consolas', 'Monaco', 'monospace']
      }
    }
  },
  plugins: []
}
