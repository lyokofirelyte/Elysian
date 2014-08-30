CHANGELOG
=========

0.8.1 (2014-08-27)
------------------

* Updated to depend on the latest version of `com.maxmind.db` and
  `com.fasterxml.jackson.core`.

0.8.0 (2014-07-22)
------------------

* The web service client API has been updated for the v2.1 release of the web
  service. In particular, the `cityIspOrg` and `omni` methods on
  `WebServiceClient` have been deprecated. The `city` method now provides all
  of the data formerly provided by `cityIspOrg`, and the `omni` method has
  been replaced by the `insights` method.
* Support was added for the GeoIP2 Connection Type, Domain, and ISP databases.

0.7.2 (2014-06-02)
------------------

* Updated to version 0.3.3 of `maxmind-db`, which fixes a potential resource
  leak when used with a thread pool.
* Updated Google HTTP Client dependency.
* The Maven build was updated to include a zip file with all dependencies.

0.7.1 (2014-04-02)
------------------

* Added `toJson` method to response objects.
* Fixed a potential issue when using the `WebServiceClient` in multi-threaded
  applications.
* Updated documentation.

0.7.0 (2013-11-05)
------------------

* Renamed `getSubdivisionsList` to `getSubdivisions` on `AbstractNamedRecord`.
* An `InputStream` constructor was added to the `DatabaseReader.Builder`
  class. This reads the stream into memory as if it was using the
  `FileMode.MEMORY` mode. Patch by Matthew Daniel.
* The source code is now attached during packaging. Patch by Matthew Daniel.

0.6.0 (2013-10-23)
------------------

* IMPORTANT API CHANGE: The `DatabaseReader` class now uses a builder to
  construct the object. The class constructor on `DatabaseReader` is no longer
  public.
* Renamed the `languages` method on the `WebServiceClient.Builder` to
  `locales`.

0.5.0 (2013-10-17)
------------------

* Reorganized the response and record classes. The response classes end
  with `Response`. The record classes no longer end in `Record`.


0.4.1 (2013-08-16)
------------------

* Set the user-agent header to include API information.
* Updated documentation.
* Removed unused dependency from Maven POM.

0.4.0 (2013-07-08)
------------------

* Removed class hierarchy among web-service endpoint models.
* Refactored database-reader API to more closely match the web-service API.
  Created a Java interface for the two classes.

0.3.0 (2013-06-27)
------------------

* Reorganized the classes. `Client` was renamed `WebServiceClient` and moved
  to `com.maxmind.geoip2`. Record classes now have a suffix of "Record".
  The product classes (e.g., Omni) were renamed to their product name with
  no "Lookup" suffix.
* Additional specific exceptions were added to replace the general
  `WebServiceException`.
* A `DatabaseReader` class was added to the distribution. This reads GeoIP2
  databases and returns similar product object to `WebServiceClient`.

0.2.0 (2013-06-13)
------------------

* Replaced the public constructor on `Client` with a `Builder` class.

0.1.1 (2013-06-10)
------------------

* First official beta release.
* Documentation updates and corrections.
* Changed license to Apache License, Version 2.0.

0.1.0 (2013-05-21)
------------------

* Initial release
