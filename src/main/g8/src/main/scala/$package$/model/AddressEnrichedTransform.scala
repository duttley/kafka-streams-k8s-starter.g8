package $package$.model

import $package$.avro.{Address, AddressEnriched}
import $package$.bigquery.BigQueryClient

object AddressEnrichedTransform {

  def from(input: Address): AddressEnriched = {

    AddressEnriched(input.line, input.postcode, "NEW FIELD")
  }
}
