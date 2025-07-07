# Apollonia 🎧

**Проект находится в стадии прототипа.**  
Основные функции (поиск, избранное, воспроизведение и текст) реализованы в базовом виде, но нуждаются в доработке.  
Остальные экраны и логика пока в разработке. Возможны баги и недоработки.

- Скачать APK (debug-подпись) [**со страницы последнего релиза**](https://github.com/Shadi965/Apollonia-android/releases/latest)
- Или с [**OneDrive**](https://1drv.ms/f/c/cb39eb4508959bd7/EnTsNC0yxLFFvRrcMkEe0KwBUq6O9AkHZXh3qmaqxb1Djg)

---

## 📸 Скриншоты

<table>
  <tr>
    <td align="center"><img src="misc/images/favorite.jpg" width="150"/><br/>Избранное</td>
    <td align="center"><img src="misc/images/search.jpg" width="150"/><br/>Поиск</td>
    <td align="center"><img src="misc/images/song_actions.jpg" width="150"/><br/>Меню песни</td>
  </tr>
  <tr>
    <td align="center"><img src="misc/images/mini_player.jpg" width="150"/><br/>Свёрнутый плеер</td>
    <td align="center"><img src="misc/images/fullscreen_player.jpg" width="150"/><br/>Развёрнутый плеер</td>
    <td align="center"><img src="misc/images/lyrics.jpg" width="150"/><br/>Экран текста</td>
    <td align="center"><img src="misc/images/playback_queue.jpg" width="150"/><br/>Очередь воспроизведения</td>
  </tr>
</table>

> Планируется переработка экранов **текста** и **очереди воспроизведения**: они будут вплетены в экран плеера.

---

## Что уже реализовано

- Поиск песен с сервера
- Просмотр и управление избранными (в том числе оффлайн)
- Воспроизведение
- Список воспроизведения с возможностью перемешивания
- Экран караоке (текст синхронизирован с песней, можно перематывать по строкам)
- Синхронизация избранного при наличии интернета

---

## Основа проекта

**Kotlin**
- **UI:** Jetpack Compose
- **Архитектура:** MVVM + Clean Architecture
- **Хранилище:** Room
- **Сетевое взаимодействие:** Retrofit + Moshi
- **Воспроизведение:** ExoPlayer
- **DI:** Hilt
- **Навигация:** Navigation Compose

---

## Серверная часть

Сервер написан на **C++** с использованием библиотеки **Crow**.  
Репозиторий: [Apollonia server](https://github.com/Shadi965/Apollonia)
