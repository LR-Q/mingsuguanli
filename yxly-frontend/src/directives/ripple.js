function createRipple(event, el) {
  const rect = el.getBoundingClientRect()
  const ripple = document.createElement('span')
  const size = Math.max(rect.width, rect.height)
  const x = event.clientX - rect.left - size / 2
  const y = event.clientY - rect.top - size / 2

  ripple.style.position = 'absolute'
  ripple.style.left = `${x}px`
  ripple.style.top = `${y}px`
  ripple.style.width = `${size}px`
  ripple.style.height = `${size}px`
  ripple.style.borderRadius = '50%'
  ripple.style.background = 'rgba(255,255,255,.35)'
  ripple.style.pointerEvents = 'none'
  ripple.style.transform = 'scale(0)'
  ripple.style.transition = 'transform 200ms cubic-bezier(0.16,1,0.3,1), opacity 320ms ease'
  ripple.style.opacity = '1'
  ripple.className = 'ripple'

  const currentPos = getComputedStyle(el).position
  el.style.position = currentPos === 'static' ? 'relative' : currentPos
  el.appendChild(ripple)

  // 强制回流以触发动画
  getComputedStyle(ripple).opacity
  ripple.style.transform = 'scale(1)'

  setTimeout(() => {
    ripple.style.opacity = '0'
    setTimeout(() => ripple.remove(), 220)
  }, 180)
}

export default {
  mounted(el) {
    el.addEventListener('click', (e) => createRipple(e, el))
  },
  beforeUnmount(el) {
    el.querySelectorAll('.ripple').forEach((n) => n.remove())
  }
}


