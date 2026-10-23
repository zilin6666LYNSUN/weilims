import Vue from 'vue'
import VueI18n from 'vue-i18n'
import Cookies from 'js-cookie'
import ElementLocale from 'element-ui/lib/locale'

// 业务语言包
import zhCN from './lang/zh-CN'
import enUS from './lang/en-US'
import jaJP from './lang/ja-JP'
import koKR from './lang/ko-KR'
import frFR from './lang/fr-FR'
import deDE from './lang/de-DE'
import esES from './lang/es-ES'
import itIT from './lang/it-IT'
import ptPT from './lang/pt-PT'
import ruRU from './lang/ru-RU'
import arSA from './lang/ar-SA'
import hiIN from './lang/hi-IN'

// Element UI 官方语言包
import elementZhCN from 'element-ui/lib/locale/lang/zh-CN'
import elementEn from 'element-ui/lib/locale/lang/en'
import elementJa from 'element-ui/lib/locale/lang/ja'
import elementKo from 'element-ui/lib/locale/lang/ko'
import elementFr from 'element-ui/lib/locale/lang/fr'
import elementDe from 'element-ui/lib/locale/lang/de'
import elementEs from 'element-ui/lib/locale/lang/es'
import elementIt from 'element-ui/lib/locale/lang/it'
import elementPt from 'element-ui/lib/locale/lang/pt'
import elementRu from 'element-ui/lib/locale/lang/ru-RU'
import elementAr from 'element-ui/lib/locale/lang/ar'

Vue.use(VueI18n)

// 12 语种切换选项（Navbar LangSelect 使用）
export const langOptions = [
  { value: 'zh-CN', label: '简体中文' },
  { value: 'en-US', label: 'English' },
  { value: 'ja-JP', label: '日本語' },
  { value: 'ko-KR', label: '한국어' },
  { value: 'fr-FR', label: 'Français' },
  { value: 'de-DE', label: 'Deutsch' },
  { value: 'es-ES', label: 'Español' },
  { value: 'it-IT', label: 'Italiano' },
  { value: 'pt-PT', label: 'Português' },
  { value: 'ru-RU', label: 'Русский' },
  { value: 'ar-SA', label: 'العربية' },
  { value: 'hi-IN', label: 'हिन्दी' }
]

const messages = {
  'zh-CN': Object.assign({}, elementZhCN, zhCN),
  'en-US': Object.assign({}, elementEn, enUS),
  'ja-JP': Object.assign({}, elementJa, jaJP),
  'ko-KR': Object.assign({}, elementKo, koKR),
  'fr-FR': Object.assign({}, elementFr, frFR),
  'de-DE': Object.assign({}, elementDe, deDE),
  'es-ES': Object.assign({}, elementEs, esES),
  'it-IT': Object.assign({}, elementIt, itIT),
  'pt-PT': Object.assign({}, elementPt, ptPT),
  'ru-RU': Object.assign({}, elementRu, ruRU),
  'ar-SA': Object.assign({}, elementAr, arSA),
  'hi-IN': Object.assign({}, elementEn, hiIN)
}

const elementMessages = {
  'zh-CN': elementZhCN,
  'en-US': elementEn,
  'ja-JP': elementJa,
  'ko-KR': elementKo,
  'fr-FR': elementFr,
  'de-DE': elementDe,
  'es-ES': elementEs,
  'it-IT': elementIt,
  'pt-PT': elementPt,
  'ru-RU': elementRu,
  'ar-SA': elementAr,
  'hi-IN': elementEn // element-ui 无印地语语言包，回退英文
}

/**
 * 获取当前语言：Cookie 优先（lang），localStorage 兜底（app_lang），默认 zh-CN
 */
export function getLang() {
  const cookieLang = Cookies.get('lang')
  const localLang = localStorage.getItem('app_lang')
  return cookieLang || localLang || 'zh-CN'
}

const i18n = new VueI18n({
  locale: getLang(),
  messages
})

/**
 * 联动 Element UI 组件语言包
 */
export function applyElementLocale(locale) {
  const elLocale = elementMessages[locale]
  if (elLocale) {
    ElementLocale.use(elLocale)
  }
}

/**
 * 切换语言：即时生效 + Cookie/localStorage 持久化 + Element locale 联动 + RTL
 */
export function setLang(locale) {
  i18n.locale = locale
  Cookies.set('lang', locale, { expires: 365 })
  localStorage.setItem('app_lang', locale)
  applyElementLocale(locale)
  const html = document.documentElement
  if (html) {
    html.setAttribute('dir', locale === 'ar-SA' ? 'rtl' : 'ltr')
    html.setAttribute('lang', locale)
  }
}

// 初始化：同步 Element locale、<html> lang/dir（支持刷新后恢复 ar-SA RTL）
const currentLang = getLang()
applyElementLocale(currentLang)
if (typeof document !== 'undefined') {
  const html = document.documentElement
  if (html) {
    html.setAttribute('dir', currentLang === 'ar-SA' ? 'rtl' : 'ltr')
    html.setAttribute('lang', currentLang)
  }
}

export default i18n
