package gov.nysenate.openleg.controller.api.bill;

import com.google.common.collect.Range;
import gov.nysenate.openleg.client.response.base.BaseResponse;
import gov.nysenate.openleg.client.response.base.ListViewResponse;
import gov.nysenate.openleg.client.response.base.ViewObjectResponse;
import gov.nysenate.openleg.client.view.bill.BillUpdateDigestView;
import gov.nysenate.openleg.client.view.bill.BillUpdateTokenDigestView;
import gov.nysenate.openleg.client.view.bill.BillUpdateTokenView;
import gov.nysenate.openleg.controller.api.base.BaseCtrl;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.PaginatedList;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.bill.data.BillUpdatesDao;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.BillUpdateToken;
import gov.nysenate.openleg.service.bill.data.BillDataService;
import gov.nysenate.openleg.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.controller.api.base.BaseCtrl.BASE_API_PATH;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Bill Updates API
 */
@RestController
@RequestMapping(value = BASE_API_PATH + "/bills", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
public class BillUpdatesCtrl extends BaseCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(BillUpdatesCtrl.class);

    @Autowired protected BillDataService billData;
    @Autowired protected BillUpdatesDao billUpdatesDao;

    /**
     * Updated Bills API
     * -----------------
     *
     * Return a list of bill ids that have changed on or after the specified date.
     * Usage: (GET) /api/3/bills/updates/{from datetime}
     *
     * Request Params: detail (boolean) - Show update digests within each token.
     *
     * Expected Output: List of BillUpdateTokenView or BillUpdateTokenDigestView if detail = true.
     *
     * @see #getUpdatesDuring(java.time.LocalDateTime, java.time.LocalDateTime, boolean, WebRequest)
     */
    @RequestMapping(value = "/updates/{from}")
    public BaseResponse getUpdatesDuring(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
                                         @RequestParam(defaultValue = "false") boolean detail, WebRequest request) {
        return getUpdatesDuring(from, DateUtils.THE_FUTURE.atStartOfDay(), detail, request);
    }

    /**
     * Updated Bills API
     * -----------------
     *
     * Return a list of bill ids that have changed during a specified date/time range.
     * Usage: (GET) /api/3/bills/updates/{from datetime}/{to datetime}
     *
     * Request Params: detail (boolean) - Show update digests within each token.
     *
     * Expected Output: List of BillUpdateTokenView or BillUpdateTokenDigestView if detail = true.
     */
    @RequestMapping(value = "/updates/{from}/{to}")
    public BaseResponse getUpdatesDuring(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
                                         @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
                                         @RequestParam(defaultValue = "false") boolean detail, WebRequest request) {
        LimitOffset limOff = getLimitOffset(request, 100);
        Range<LocalDateTime> updateRange = Range.closedOpen(from, to);
        PaginatedList<BillUpdateToken> updateTokens = billUpdatesDao.billsUpdatedDuring(updateRange, SortOrder.ASC, limOff);
        return ListViewResponse.of(
            updateTokens.getResults().stream()
                .map(token ->
                        (detail) ? new BillUpdateTokenDigestView(token, billUpdatesDao.getUpdateDigests(
                                                                        token.getBillId(), updateRange, SortOrder.ASC))
                               : new BillUpdateTokenView(token))
                .collect(Collectors.toList()), updateTokens.getTotal(), limOff);
    }

    /**
     * Bill Update Digests API
     * ------------------------
     *
     * Return all the update digests for a given bill.
     * Usage (GET) /api/3/bills/{sessionYear}/{printNo}/updates
     *
     * @see #getUpdatesForBillDuring(int, String, java.time.LocalDateTime, java.time.LocalDateTime)
     */
    @RequestMapping(value = "/{sessionYear:[\\d]{4}}/{printNo}/updates")
    public BaseResponse getUpdatesForBill(@PathVariable int sessionYear, @PathVariable String printNo) {
        return getUpdatesForBillDuring(sessionYear, printNo, DateUtils.LONG_AGO.atStartOfDay(), DateUtils.THE_FUTURE.atStartOfDay());
    }

    /**
     * Bill Update Digests API
     * ------------------------
     *
     * Return update digests for a bill during a given date/time range
     * Usage (GET) /api/3/bills/{sessionYear}/{printNo}/updates/{from}/{to}
     *
     * Note: 'from' and 'to' are ISO date times.
     *
     * Expected Output: List of BillUpdateDigestView
     */
    @RequestMapping(value = "/{sessionYear:[\\d]{4}}/{printNo}/updates/{from}/{to}")
    public BaseResponse getUpdatesForBillDuring(@PathVariable int sessionYear, @PathVariable String printNo,
                                          @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
                                          @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        List<BillUpdateDigestView> digests =
           billUpdatesDao.getUpdateDigests(new BaseBillId(printNo, sessionYear), Range.openClosed(from, to), SortOrder.ASC)
                .stream().map(BillUpdateDigestView::new)
                .collect(Collectors.toList());
        return ListViewResponse.of(digests, digests.size(), LimitOffset.ALL);
    }
}