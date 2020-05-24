package io.easybreezy.project.application.issue.queryobject

import io.easybreezy.infrastructure.query.ContinuousList
import io.easybreezy.infrastructure.query.PagingParameters
import io.easybreezy.infrastructure.query.QueryObject
import io.easybreezy.infrastructure.serialization.UUIDSerializer
import io.easybreezy.project.model.issue.Issues
import kotlinx.serialization.Serializable
import java.util.UUID
import io.easybreezy.infrastructure.query.toContinuousList
import io.easybreezy.project.model.Projects
import io.easybreezy.project.model.issue.IssueLabel
import io.easybreezy.project.model.issue.Labels
import io.easybreezy.project.model.issue.PriorityTable
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select

class HasIssuesInCategoryQO(private val inCategory: UUID) : QueryObject<Boolean> {
    override suspend fun getData() =
        Issues.select { Issues.category eq inCategory }.count() > 0
}

class HasIssuesInStatusQO(private val inStatus: UUID) : QueryObject<Boolean> {
    override suspend fun getData() =
        Issues.select { Issues.status eq inStatus }.count() > 0
}

class IssueQO(private val id: UUID) : QueryObject<IssueDetails> {
    override suspend fun getData() =
        Issues
            .leftJoin(IssueLabel)
            .join(Labels, JoinType.LEFT, IssueLabel.label, Labels.id)
            .select {
                Issues.id eq id
            }
            .toIssueDetails()
            .single()
}

fun Iterable<ResultRow>.toIssueDetails(): List<IssueDetails> {
    return fold(mutableMapOf<UUID, IssueDetails>()) { map, resultRow ->
        val details = resultRow.toIssueDetails()
        val current = map.getOrDefault(details.id, details)

        val labelId = resultRow.getOrNull(Labels.id)
        val labels = labelId?.let { resultRow.toLabel() }

        map[details.id] = current.copy(
            labels = current.labels.plus(listOfNotNull(labels)).distinct()
        )
        map
    }.values.toList()
}

class IssuesQO(private val paging: PagingParameters, private val project: String) : QueryObject<ContinuousList<Issue>> {
    override suspend fun getData() =
        Issues
            .join(Projects, JoinType.INNER, Issues.project, Projects.id)
            .select {
                Projects.slug eq project
            }
            .orderBy(Issues.priority[PriorityTable.value] to SortOrder.DESC, Issues.createdAt to SortOrder.DESC)
            .toContinuousList(paging, ResultRow::toIssue)
}

fun ResultRow.toIssue() = Issue(
    this[Issues.id].value,
    this[Issues.title],
    this[Issues.priority[PriorityTable.color]]?.rgb
)

fun ResultRow.toIssueDetails() = IssueDetails(
    this[Issues.id].value,
    this[Issues.title],
    this[Issues.priority[PriorityTable.color]]?.rgb
)

fun ResultRow.toLabel() = Label(
    this[Labels.id].value,
    this[Labels.name]
)

@Serializable
data class Label(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    val name: String
)

@Serializable
data class Issue(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    val title: String,
    val priority: String?
)

@Serializable
data class IssueDetails(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    val title: String,
    val priority: String?,
    val labels: List<Label> = listOf()
)
