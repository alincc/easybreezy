package io.easybreezy.infrastructure.query

import com.google.inject.Inject
import io.easybreezy.infrastructure.exposed.TransactionManager

class QueryExecutor @Inject constructor(private val transactional: TransactionManager) {
    suspend fun <T> execute(queryObject: QueryObject<T>): T {
        return transactional {queryObject.getData() }
    }
}
