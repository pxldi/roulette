import slick.jdbc.JdbcBackend.Database
import com.typesafe.config.ConfigFactory

object DatabaseConfig {
  private val config = ConfigFactory.load()
  private val dbUrl = config.getString("db.url")
  private val dbUser = config.getString("db.user")
  private val dbPassword = config.getString("db.password")

  val db: Database = Database.forURL(dbUrl, user = dbUser, password = dbPassword, driver = "org.postgresql.Driver")
}