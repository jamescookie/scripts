import groovy.xml.*
import org.ccil.cowan.tagsoup.Parser

def tagsoupParser = new Parser()
def slurper = new XmlSlurper(tagsoupParser)
def htmlParser = slurper.parse(Thread.currentThread().contextClassLoader.getResourceAsStream('shopping.html'))
def unwantedClasses = [
        'product-image',
        'screenReaderOnly',
        'promotions-wrapper',
        'receipt-total-price',
        'receipt-item-price',
        'receipt-total',
        'receipt-fulfilled-heading',
]
def unwantedNodes = ['div', 'svg', 'fieldset', 'img', 'span', 'h4']

htmlParser.'**'
        .findAll { node ->
            unwantedNodes.contains(node.name()) &&
//                    (node.@class.toString().split().any { className -> unwantedClasses.any { className.contains(it) } } ||
                    node.@'data-testid'.toString().split().any { className -> unwantedClasses.any { className.contains(it) } }//)
        }
        .forEach {
            if (it) it.replaceNode { '' }
        }
new File('build', 'list.html').newWriter().withWriter { w ->
    w << XmlUtil.serialize(htmlParser)
            .replaceAll('tag0:', '')
            .replaceAll('class=".*?"', '')
            .replaceAll('data-testid=".*?"', '')
            .replaceAll('\\s+>', '>')
            .replaceAll('<span>(.*)</span>', '$1')
            .replaceAll('<a.*?>(.*)</a>', '$1')
            .replaceAll('Quantity\\s*\\d+', '')
            .replaceAll('<div>\\s*</div>', '')
            .replaceAll('<div />', '')
            .replaceAll('<picture/>', '')
            .replaceAll('\\s*\\n', '\n')
            .replaceAll('\\n\\n', '\n')
            .replaceAll('article', 'ul')
}
println('done')
