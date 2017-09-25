# hiJack
hiJack will (using crt.sh) collect subdomains of your target.
if the target subdomains contain any cnames that are not registered, it will let you know.

Inspired by @seckle_ch talk at bsides zuerich
https://twitter.com/seckle_ch/status/912221121910575105


# Usage
Example of a "safe" target:
```
/Desktop$ java -jar hijack.jar oz-web.com
Starting
Looking for cnames in 3 subdomains
[www.b.oz-web.com, www.oz-web.com, b.oz-web.com]

DONE
```
