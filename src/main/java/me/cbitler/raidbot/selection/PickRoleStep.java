package me.cbitler.raidbot.selection;

import me.cbitler.raidbot.raids.Raid;
import me.cbitler.raidbot.raids.RaidUser;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;

/**
 * Step for picking a role for a raid
 * @author Christopher Bitler
 * @author Franziska Mueller
 */
public class PickRoleStep implements SelectionStep {
    Raid raid;
    String spec;
    boolean isFlexRole;

    /**
     * Create a new step for this role selection with the specified raid and spec
     * that the user chose
     * @param raid The raid
     * @param spec The specialization that the user chose
     */
    public PickRoleStep(Raid raid, String spec, boolean isflex) {
        this.raid = raid;
        this.spec = spec;
        this.isFlexRole = isflex;
    }

    /**
     * Handle the user input - checks to see if the role they are picking is valid
     * and not full, and if so, adding them to that role
     * @param e The private message event
     * @return True if the user chose a valid, not full, role, false otherwise
     */
    @Override
    public boolean handleDM(PrivateMessageReceivedEvent e) {
    	boolean success = true;
    	try {
    		int roleId = Integer.parseInt(e.getMessage().getRawContent()) - 1;
    		String roleName = raid.getRoles().get(roleId).getName();
    		pickRole(e.getAuthor().getId(), e.getAuthor().getName(), roleName);
    	} catch (Exception exp) {
    		success = false;	
    	}
    	
        if(success) {
            e.getChannel().sendMessage("Added to event roster" + (isFlexRole ? " as flex role" : "") + ".").queue();
        } else {
            e.getChannel().sendMessage("Please choose a valid role" + (isFlexRole ? "" : " that is not full") + ".").queue();
        }
        return success;
    }

    /**
     * Get the next step - no next step here as this is a one step process
     * @return null
     */
    @Override
    public SelectionStep getNextStep() {
        return null;
    }

    /**
     * The step text changes the text based on the available roles.
     * @return The step text
     */
    @Override
    public String getStepText() {
        String text = "Pick a role:\n";
        for (int i = 0; i < raid.getRoles().size(); i++) {
            text += "`" + (i+1) + "` " + raid.getRoles().get(i).getName() + "\n";
        }
        text += "or type cancel to cancel role selection.";

        return text;
    }
    
    /**
     * adds the user as the default role (first role)
     * */
    public void pickRole(String userID, String username, String roleName) {
    	if (isFlexRole) {
			if(raid.isValidRole(roleName)) {
                raid.addUserFlexRole(userID, username, spec, roleName, true, true);
            }
		} else {
			if(raid.isValidNotFullRole(roleName)) {
				raid.addUser(userID, username, spec, roleName, true, true);
			}
		}
    	
    	
    }
}