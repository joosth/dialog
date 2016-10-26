package org.open_t.dialog

import org.apache.commons.logging.LogFactory
import groovy.time.TimeCategory

/**
 * Dynamic date & time calculation
 */
class DateTimeUtil {

    private static final log = LogFactory.getLog(this)

    /* A date may be specified as an exact value or a relative one. */
    public static Date determineDate (String dateString) {

        Date newDate = null

        log.trace "determining date.."
        log.debug "dateString: ${dateString}"
        if (dateString) {
            newDate = determineDateIso(dateString)

            if (!newDate) {
                // Integer
                if (dateString.isInteger()) {
                    newDate = this.determineDateOffsetNumeric(dateString.toInteger())
                }
                // String
                else {
                    newDate = this.determineDateOffsetString(dateString)
                }
            }

            if (newDate) {
                newDate.clearTime()
            }
        }

        return newDate
    }

    private static Date determineDateIso(String dateString) {

        log.trace "determining date as ISO date.."
        try {
            return Date.parse("yyyy-MM-dd", dateString)
        }
        catch (java.text.ParseException e) {
            log.trace "not a ISO date, returning null.."
            return null
        }
    }

    private static Date determineDateOffsetNumeric (Integer offsetNumeric) {

        log.trace "determining date as integer in days from current date.."
        Date date = new Date()

        log.debug "offsetNumeric: ${offsetNumeric}"
        date = date.plus(offsetNumeric)

        return date
    }

    private static Date determineDateOffsetString (String offsetString) {

        log.trace "determining date as offset string from current date.."
        Date date = new Date()

        try {
        
            def offsetItems = offsetString.split(" ")
            offsetItems.each { offsetItem ->
                
                def offsetOperator = offsetItem.substring(0, 1)
                log.debug "offsetOperator: ${offsetOperator}"
                def offsetValue = offsetItem.substring(1, offsetItem.length() - 1).toInteger()
                log.debug "offsetValue: ${offsetValue}"
                def offsetUnit = offsetItem.substring(offsetItem.length() - 1, offsetItem.length()).toLowerCase()
                log.debug "offsetUnit: ${offsetUnit}"

                use (TimeCategory) {

                    switch (offsetOperator) {

                        case "+":
                            switch (offsetUnit) {
                                case "d":
                                    date = date + offsetValue.days
                                    break
                                case "w":
                                    date = date + offsetValue.weeks
                                    break
                                case "m":
                                    date = date + offsetValue.months
                                    break
                                case "y":
                                    date = date + offsetValue.years
                                    break
                                default:
                                    throw new Exception("Unknown offset unit!")
                                    break
                            }
                            break

                        case "-":
                            switch (offsetUnit) {
                                case "d":
                                    date = date - offsetValue.days
                                    break
                                case "w":
                                    date = date - offsetValue.weeks
                                    break
                                case "m":
                                    date = date - offsetValue.months
                                    break
                                case "y":
                                    date = date - offsetValue.years
                                    break
                                default:
                                    throw new Exception("Unknown offset unit!")
                                    break
                            }
                            break

                        default:
                            throw new Exception("Unknown offset operator!")
                            break
                    }
                }
            }
        }
        catch (Exception e) {
            log.error "Error parsing offset string '${offsetString}'! (${e.message})"
            return null
        }

        return date
    }

    /* A time may be specified as an exact value */
    public static Date determineTime (String timeString) {

        Date time = null

        log.trace "determining time.."
        log.debug "timeString: ${timeString}"
        if (timeString) {
            time = determineTimeIso(timeString)
        }

        return time
    }

    private static Date determineTimeIso(String timeString) {

        Date time = null

        log.trace "determining time as ISO time.."
        try {
            switch (timeString.length()) {
                case 2:
                    time = Date.parse("HH", timeString)
                    break
                case 5:
                    time = Date.parse("HH:mm", timeString)
                    break
                case 8:
                    time = Date.parse("HH:mm:ss", timeString)
                    break
                default:
                    log.error "Wrong time string '${timeString}'! Returning null."
                    return null
                    break
            }
        }
        catch (java.text.ParseException e) {
            log.trace "not a ISO time, returning null.."
            return null
        }

        return time
    }

}
