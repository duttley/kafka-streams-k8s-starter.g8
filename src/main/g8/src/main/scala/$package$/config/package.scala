package $package$

package object config {

  case class Config(kafka: KafkaConfig, caci: CaciConfig)

  object Config {
    import pureconfig._
    import pureconfig.generic.auto._

    def load(configFile: String = "application.conf"): Config = {
      ConfigSource.default.loadOrThrow[Config]
    }
  }
}
