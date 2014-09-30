# jSimHash [![Build Status](https://api.travis-ci.org/tomtung/jsimhash)](https://travis-ci.org/tomtung/jsimhash)

jSimHash is a simple JVM library for building [simhash](http://www.cs.princeton.edu/courses/archive/spr04/cos598B/bib/CharikarEstim.pdf) fingerprints and using them to [detect near-duplications](http://www.wwwconference.org/www2007/papers/paper215.pdf) in data, especially text documents.

## Example

```java
import com.google.common.base.Strings;
import com.github.tomtung.simhash.SimHashBuilder;
import com.github.tomtung.simhash.SimHashIndex;
import com.github.tomtung.simhash.Util;

import java.util.HashMap;

public class Main {
    static SimHashBuilder simHashBuilder = new SimHashBuilder();

    static long computeStringFingerprint(String s) {
        simHashBuilder.reset();
        int shinglingLength = 3;
        s = s.replaceAll("[^\\w,]+", " ").toLowerCase();
        for (int i = 0; i < s.length() - shinglingLength; i += 1) {
            simHashBuilder.addStringFeature(s.substring(i, i + shinglingLength));
        }
        return simHashBuilder.computeResult();
    }

    public static void main(String[] args) {

        System.out.println("Strings and their fingerprints:");
        System.out.println(Strings.repeat("-", 90));

        String query = "Park Beach Interiors, Park Beach Plaza Pacific Hwy, Coffs Harbour, NSW, 2450";
        long queryFingerprint = computeStringFingerprint(query);
        System.out.format("Query string:\n" + "%s\n" + "%s\n\n", query, Util.simHashToString(queryFingerprint));


        System.out.println("Test strings:\n");

        String[] strings = {
                "Park Beach Interiors, 26 Park Beach Plaza, Pacific Hwy, Coffs Harbour, NSW, 2450",
                "Park Beach Interiors, Showroom Park Beach Plaza Pacific Hwy, Coffs Harbour, NSW, 2450",
                "Park Beach Interiors, Showroom Park Beach Plaza Pacific Highway, Coffs Harbour, NSW, 2450",
                "Weaver Interiors, 997 Pacific Hwy, Pymble, NSW, 2073",
                "One Stop Bakery, 1304 High Street Rd, Wantirna South, VIC, 3152",
                "This is not an address."
        };

        SimHashIndex simHashIndex = new SimHashIndex(4);

        HashMap<Long, String> fingerPrintToString = new HashMap<>();
        for (String s : strings) {
            long fingerprint = computeStringFingerprint(s);
            System.out.format(
                    "%s\n" + "%s\n" + "Hamming distance to query string: %d\n\n",
                    s,
                    Util.simHashToString(fingerprint),
                    Util.hammingDistance(queryFingerprint, fingerprint)
            );
            fingerPrintToString.put(fingerprint, s);
            simHashIndex.add(fingerprint);
        }

        System.out.println();
        System.out.println("Find near duplicates for query string:");
        System.out.println(Strings.repeat("-", 90));

        long[] duplicateFingerprints = simHashIndex.findNearDuplicates(queryFingerprint);
        for (long f : duplicateFingerprints) {
            System.out.println(fingerPrintToString.get(f));
        }
    }
}
```

Output:

```
Strings and their fingerprints:
------------------------------------------------------------------------------------------
Query string:
Park Beach Interiors, Park Beach Plaza Pacific Hwy, Coffs Harbour, NSW, 2450
1111111111111111111111111111111111100111111111110110010111110011

Test strings:

Park Beach Interiors, 26 Park Beach Plaza, Pacific Hwy, Coffs Harbour, NSW, 2450
1111111111111111111111111111111111100111111101111110110111110011
Hamming distance to query string: 3

Park Beach Interiors, Showroom Park Beach Plaza Pacific Hwy, Coffs Harbour, NSW, 2450
1111111111111111111111111111111111100101111111110110110111110011
Hamming distance to query string: 2

Park Beach Interiors, Showroom Park Beach Plaza Pacific Highway, Coffs Harbour, NSW, 2450
1111111111111111111111111111111111000110111111110110111111110011
Hamming distance to query string: 4

Weaver Interiors, 997 Pacific Hwy, Pymble, NSW, 2073
1111111111111111111111111111111111011111101001111110110111110111
Hamming distance to query string: 9

One Stop Bakery, 1304 High Street Rd, Wantirna South, VIC, 3152
1111111111111111111111111111111111011101111111010111110110110001
Hamming distance to query string: 9

This is not an address.
1111111111111111111111111111111111111111000001011011011011000010
Hamming distance to query string: 16


Find near duplicates for query string:
------------------------------------------------------------------------------------------
Park Beach Interiors, 26 Park Beach Plaza, Pacific Hwy, Coffs Harbour, NSW, 2450
Park Beach Interiors, Showroom Park Beach Plaza Pacific Hwy, Coffs Harbour, NSW, 2450
Park Beach Interiors, Showroom Park Beach Plaza Pacific Highway, Coffs Harbour, NSW, 2450
```

## Add as dependency in Maven / SBT projects

To add dependency on jSimHash, insert the following to your `pom.xml` file if you use Apache Maven:

```xml
<repositories>
    <!-- Other repositories ... -->
    <repository>
        <id>Sonatype OSS Snapshots</id>
        <url>https://oss.sonatype.org/content/repositories/snapshots/</url>
    </repository>
</repositories>

<dependencies>
    <!-- Other dependencies ... -->
    <dependency>
        <groupId>com.github.tomtung</groupId>
        <artifactId>jsimhash</artifactId>
        <version>0.1-SNAPSHOT</version>
    </dependency>
</dependencies>
```

or add the following to your `build.sbt` file if you use sbt 0.11+:

```scala
libraryDependencies += "com.github.tomtung" % "jsimhash" % "0.1-SNAPSHOT"

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"
```


## References

- [Charikar, M. S. (2002). Similarity Estimation Techniques from Rounding Algorithms. In Proceedings of the Thiry-fourth Annual ACM Symposium on Theory of Computing](http://www.cs.princeton.edu/courses/archive/spr04/cos598B/bib/CharikarEstim.pdf)
- [Manku, G. S., Jain, A., & Das Sarma, A. (2007). Detecting Near-duplicates for Web Crawling. In Proceedings of the 16th International Conference on World Wide Web](http://www.wwwconference.org/www2007/papers/paper215.pdf)
- [the simhash algorithm](http://matpalm.com/resemblance/simhash/) by [@mat_kelcey](https://twitter.com/mat_kelcey)
- [liangsun/simhash](https://github.com/liangsun/simhash): a similar Python library
