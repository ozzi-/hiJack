# hiJack
hiJack will (using crt.sh) collect subdomains of your target.
if the target subdomains contain any cnames that are not registered, it will let you know.

Inspired by @seckle_ch talk at bsides zuerich
https://twitter.com/seckle_ch/status/912221121910575105


# Usage
Example of a "safe" target which adds an additional subdomain file (1 host per line) and a specific DNS resolver
**/Desktop$ java -jar hijack.jar oz-web.com /home/ozzi/additionalSubdomainFile 172.0.100.100**
```
Starting
3 subdomains found via crt.sh dork
Testing for AXFR transfer with [ns3.hosttech.ch, ns2.hosttech.ch, ns1.hosttech.ch]
All AXFR transfers failed
2 subdomains provided via list /home/ozzi/Desktop/list, effectively added: 2
5 total number of subdomains that will be checked
[www.b.oz-web.com, www.oz-web.com, b.oz-web.com, c.oz-web.com, d.oz-web.com]

Found no hijack possibilities ...
Done

```
