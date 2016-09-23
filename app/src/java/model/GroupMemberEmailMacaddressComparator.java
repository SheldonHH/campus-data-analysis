package model;

import java.util.Comparator;

/**
 * Sorts Group Detection Result according to group member's mac-address and email
 * 
 */
public class GroupMemberEmailMacaddressComparator implements Comparator<GroupMember> {

    public int compare(GroupMember a, GroupMember b) {
        String aEmail = a.getEmail();
        String bEmail = b.getEmail();
        String aMac = a.getMac_Address();
        String bMac = b.getMac_Address();
        if (aEmail.isEmpty() && bEmail.isEmpty()) {
            return aMac.compareTo(bMac);
        } else {
            if (aEmail.isEmpty()) {
                return 1;
            } else if (bEmail.isEmpty()) {
                return -1;
            } else {
                return aEmail.compareTo(bEmail);
            }
        }
    }
}
