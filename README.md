# jSimHash [![Build Status](https://travis-ci.org/tomtung/jsimhash.svg?branch=master)](https://travis-ci.org/tomtung/jsimhash)

jSimHash is a simple JVM library for building [simhash](http://www.cs.princeton.edu/courses/archive/spr04/cos598B/bib/CharikarEstim.pdf) fingerprints and using them to [detect near-duplications](http://www.wwwconference.org/www2007/papers/paper215.pdf) in data, especially text documents.

## Example

```java
import com.google.common.base.Strings;
import com.github.tomtung.jsimhash.*;

import java.util.HashMap;

public class Main {
    static SimHashBuilder simHashBuilder = new SimHashBuilder();

    static long computeStringFingerprint(String s) {
        simHashBuilder.reset();
        int shinglingLength = 2;
        s = s.replaceAll("[^\\w,]+", " ").toLowerCase();
        for (int i = 0; i <= s.length() - shinglingLength; i += 1) {
            simHashBuilder.addStringFeature(s.substring(i, i + shinglingLength));
        }
        return simHashBuilder.computeResult();
    }

    public static void main(String[] args) {

        System.out.println("Strings and their fingerprints:");
        System.out.println(Strings.repeat("-", 90));

        String query =  "Park Beach Interiors, Showroom Park Beach Plaza Pacific Hwy, Coffs Harbour, NSW, 2450";
        long queryFingerprint = computeStringFingerprint(query);
        System.out.format("Query string:\n" + "%s\n" + "%s\n\n", query, Util.simHashToString(queryFingerprint));


        System.out.println("Test strings:\n");

        String[] strings = {
                "Park Beach Interiors, Park Beach Plaza Pacific Hwy, Coffs Harbour, NSW, 2450",
                "Park Beach Interiors, 26 Park Beach Plaza, Pacific Hwy, Coffs Harbour, NSW, 2450",
                "Park Beach Interiors, Showroom Park Beach Plaza Pacific Highway, Coffs Harbour, NSW, 2450",
                "Weaver Interiors, 997 Pacific Hwy, Pymble, NSW, 2073",
                "One Stop Bakery, 1304 High Street Rd, Wantirna South, VIC, 3152",
                "This is not an address."
        };

        SimHashIndex simHashIndex = new SimHashIndex(6);

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
Park Beach Interiors, Showroom Park Beach Plaza Pacific Hwy, Coffs Harbour, NSW, 2450
0100000111011000001000100000111001101000011110101100101011011010

Test strings:

Park Beach Interiors, Park Beach Plaza Pacific Hwy, Coffs Harbour, NSW, 2450
0100000111111000001000100000111001101000010110101100101001011010
Hamming distance to query string: 3

Park Beach Interiors, 26 Park Beach Plaza, Pacific Hwy, Coffs Harbour, NSW, 2450
0101001101111000001000100000111001101000010110101100101011011000
Hamming distance to query string: 6

Park Beach Interiors, Showroom Park Beach Plaza Pacific Highway, Coffs Harbour, NSW, 2450
0100000111111001001001100000111001101000011110001100001011011010
Hamming distance to query string: 5

Weaver Interiors, 997 Pacific Hwy, Pymble, NSW, 2073
1100001101011101000000100000101011001101101001001110110100001101
Hamming distance to query string: 27

One Stop Bakery, 1304 High Street Rd, Wantirna South, VIC, 3152
0110101101000100010001001010110001101100011101101010011101111100
Hamming distance to query string: 26

This is not an address.
0010110010000100100101001000110000011001011100000001011010010010
Hamming distance to query string: 29


Find near duplicates for query string:
------------------------------------------------------------------------------------------
Park Beach Interiors, Showroom Park Beach Plaza Pacific Highway, Coffs Harbour, NSW, 2450
Park Beach Interiors, Park Beach Plaza Pacific Hwy, Coffs Harbour, NSW, 2450
Park Beach Interiors, 26 Park Beach Plaza, Pacific Hwy, Coffs Harbour, NSW, 2450
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
        <artifactId>jsimhash_2.11</artifactId>
        <version>0.1-SNAPSHOT</version>
    </dependency>
</dependencies>
```

or add the following to your `build.sbt` file if you use sbt 0.11+:

```scala
libraryDependencies += "com.github.tomtung" %% "jsimhash" % "0.1-SNAPSHOT"

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"
```


## References

- [Charikar, M. S. (2002). Similarity Estimation Techniques from Rounding Algorithms. In Proceedings of the Thiry-fourth Annual ACM Symposium on Theory of Computing](http://www.cs.princeton.edu/courses/archive/spr04/cos598B/bib/CharikarEstim.pdf)
- [Manku, G. S., Jain, A., & Das Sarma, A. (2007). Detecting Near-duplicates for Web Crawling. In Proceedings of the 16th International Conference on World Wide Web](http://www.wwwconference.org/www2007/papers/paper215.pdf)
- [the simhash algorithm](http://matpalm.com/resemblance/simhash/) by [@mat_kelcey](https://twitter.com/mat_kelcey)
- [liangsun/simhash](https://github.com/liangsun/simhash): a similar Python library
