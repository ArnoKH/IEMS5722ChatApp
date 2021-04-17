package hk.edu.cuhk.ie.iems5722.a4_1155152392.parser

import android.nfc.NdefMessage
import android.nfc.NdefRecord

object NdefMessageParser {
    @JvmStatic fun parse(message: NdefMessage): List<ParsedNdefRecord> {
        return getRecords(message.records)
    }

    @JvmStatic fun getRecords(records: Array<NdefRecord>): List<ParsedNdefRecord> {
        val elements = ArrayList<ParsedNdefRecord>()

        for (record in records) {
            if (TextRecord.isText(record)) {
                elements.add(TextRecord.parse(record))
            } else {
                elements.add(object : ParsedNdefRecord {
                    override fun str(): String {
                        return String(record.payload)
                    }
                })
            }
        }
        return elements
    }

    @JvmStatic fun parserNDEFMessage(messages: List<NdefMessage>): String {
        val builder = StringBuilder()
        val records = parse(messages[0])
        val size = records.size

        for (i in 0 until size) {
            val record = records.get(i)
            val str = record.str()
            builder.append(str).append("\n")
        }
        return builder.toString()
    }
}