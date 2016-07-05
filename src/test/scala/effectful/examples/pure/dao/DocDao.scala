package effectful.examples.pure.dao

import java.time.Instant

/**
  * A simple document store
  *
  * @tparam ID type of the identifier
  * @tparam A type of the record
  * @tparam Q the native query type
  * @tparam E effect system
  */
trait DocDao[ID,A,Q,E[_]] {
  import DocDao._

  /** @return TRUE if id is in use FALSE otherwise */
  def exists(id: ID) : E[Boolean]
  /** @return empty sequence if all IDs exists otherwise the set of IDs that exist */
  def batchExists(ids: Traversable[ID]) : E[Set[ID]]

  /** @return if id exists, some value otherwise none */
  def findById(id: ID) : E[Option[(ID,A,RecordMetadata)]]

  def findByNativeQuery(q: Q) : E[Vector[(ID,A,RecordMetadata)]]

  /** @return a batch of all records starting with specified index and batch size (records are sorted by creation time) */
  def findAll(start: Int, batchSize: Int) : E[Vector[(ID,A,RecordMetadata)]]

  /** @return TRUE if document was inserted FALSE if id already exists */
  def insert(id: ID, a : A) : E[Boolean]
  /** @return count of documents successfully inserted */
  def batchInsert(records: Traversable[(ID,A)]) : E[Int]
  /** @return TRUE if document was updated FALSE if id doesn't exist */
  def update(id: ID, value: A) : E[Boolean]

  def batchUpdate(records: Traversable[(ID,A)]) : E[Int]

  def upsert(id: ID, a : A) : E[(Boolean,Boolean)]
  def batchUpsert(records: Traversable[(ID,A)]) : E[(Int,Int)]

  /** @return TRUE if document was marked as removed FALSE if id doesn't exist */
  def remove(id: ID) : E[Boolean]
  def batchRemove(ids: Traversable[ID]) : E[Int]
}

object DocDao {
  case class RecordMetadata(
    created: Instant,
    lastUpdated: Instant,
    removed: Option[Instant]
  )
  object RecordMetadata {
    def forNewRecord = RecordMetadata(
      created = Instant.now(),
      lastUpdated = Instant.now(),
      removed = None
    )
  }
}