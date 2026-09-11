package ru.yandex.praktikumchatapp.theory

/*
// Использование:
val db = SingletonExample.getInstance()
db.query("SELECT * FROM users")


В функции getInstance() создаётся экземпляр класса базы данных с помощью способа,
который называется double-checked locking. Он гарантирует потокобезопасность благодаря @Volatile
и использованию synchronized. В реализации синглтона языка Kotlin object под капотом используется подобный способ,
 гарантирующий потокобезопасность.
 */
class SingletonExample private constructor() {

    companion object {
        @Volatile
        private var instance: SingletonExample? = null

        fun getInstance(): SingletonExample {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = SingletonExample()
                    }
                }
            }
            return instance!!
        }
    }

    fun query(sql: String) {
        println("Выполняем запрос: $sql")
    }
}