# springMedia

**Цель проекта и требования**:
  * Разработать RESTful API для социальной медиа платформы,
    позволяющей пользователям регистрироваться, входить в систему, создавать
    посты, переписываться, подписываться на других пользователей и получать
    свою ленту активности.
- Аутентификация и авторизация:
  * Пользователи могут зарегистрироваться, указав имя пользователя, электронную почту и пароль.
  * Пользователи могут войти в систему, предоставив правильные учетные данные.
  * API должен обеспечивать защиту конфиденциальности пользовательских данных, включая хэширование паролей и использование JWT.
- Управление постами:
  * Пользователи могут создавать новые посты, указывая текст, заголовок и прикрепляя изображения
  * Пользователи могут просматривать посты других пользователей
  * Пользователи могут обновлять и удалять собственные посты
- Взаимодействие пользователей:
  * Пользователи могут отправлять заявки в друзья другим пользователям. С
    этого момента, пользователь, отправивший заявку, остается подписчиком до
    тех пор, пока сам не откажется от подписки. Если пользователь, получивший
    заявку, принимает ее, оба пользователя становятся друзьями. Если отклонит,
    то пользователь, отправивший заявку, как и указано ранее, все равно остается
    подписчиком.
  * Пользователи, являющиеся друзьями, также являются подписчиками друг
    на друга.
  * Если один из друзей удаляет другого из друзей, то он также отписывается.
    Второй пользователь при этом должен остаться подписчиком.
  * Друзья могут писать друг другу сообщения (реализация чата не нужна,
    пользователи могу запросить переписку с помощью запроса)
- Подписки и лента активности:
  * Друзья могут писать друг другу сообщения (реализация чата не нужна,
    пользователи могу запросить переписку с помощью запроса)
  * Лента активности должна поддерживать пагинацию и сортировку по
    времени создания постов.
- Обработка ошибок:
  * API должно обрабатывать и возвращать понятные сообщения об ошибках
    при неправильном запросе или внутренних проблемах сервера.
  * API должно осуществлять валидацию введенных данных и возвращать
    информативные сообщения при неправильном формате
- Документация API:
  * PI должно быть хорошо задокументировано с использованием
    инструментов, таких как Swagger или OpenAPI.
  * Документация должна содержать описания доступных эндпоинтов,
    форматы запросов и ответов, а также требования к аутентификации.
- Технологии и инструменты:
  * Язык программирования: Java
  * Фреймворк: Spring (рекомендуется использовать Spring Boot)
  * База данных: Рекомендуется использовать PostgreSQL или MySQL
  * Аутентификация и авторизация: Spring Security
  * Документация API: Swagger или OpenAPI

**Реализация**:
* IDE IntelliJ IDEA
* Язык программирования: Java openjdk-20.0.1
* Фреймворк: Spring Boot (v3.0.5)
* База данных: PostgreSQL (H2 при тестировании)
* Аутентификация и авторизация: Spring Security (вариант с хранимыми токенами)
* Документация API: OpenAPI 3
