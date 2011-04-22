/*
 * List Transfer Handler
 * 
 * This code has been adapted from:
 * "How to Use Drag and Drop and Data Transfer", 
 * from "The Java Tutorial" (Sun Microsystems).
 */

package browser;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.TransferHandler;


public class ListTransferHandler extends TransferHandler
{
    private int[] indices = null;

    // Take the incoming string and wherever there is a
    // newline, break it into a separate item in the list.
    protected void importString(JComponent c, String str)
    {
        JList target = (JList)c;
        DefaultListModel listModel = (DefaultListModel)target.getModel();
        int index = target.getSelectedIndex();

        // Prevent the user from dropping data back on itself.
        // For example, if the user is moving items #4,#5,#6 and #7 and
        // attempts to insert the items after item #5, this would
        // be problematic when removing the original items.
        // So this is not allowed.
        if (indices != null && index >= indices[0] - 1 &&
              index <= indices[indices.length - 1])
        {
            indices = null;
            return;
        }

        int max = listModel.getSize();
        if (index < 0)
        {
            index = max;
        } 
        else
        {
            index++;
            if (index > max)
            {
                index = max;
            }
        }
        String[] values = str.split("\n");
        for (int i = 0; i < values.length; i++)
        {
            listModel.add(index++, values[i]);
        }
    }
    
    public int getSourceActions(JComponent c) 
    {
        return COPY_OR_MOVE;
    }
    
    public boolean importData(JComponent c, Transferable t) 
    {
        if (canImport(c, t.getTransferDataFlavors())) 
        {
            try 
            {
                String str = (String)t.getTransferData(DataFlavor.stringFlavor);
                importString(c, str);
                return true;
            } catch (UnsupportedFlavorException ufe) 
            {
            } 
            catch (IOException ioe) 
            {
            }
        }

        return false;
    }
    
    protected void exportDone(JComponent c, Transferable data, int action) 
    {
    	// This custom Transfer Handler prevents the export of strings.
    }
    
    public boolean canImport(JComponent c, DataFlavor[] flavors) 
    {
        for (int i = 0; i < flavors.length; i++) 
        {
            if (DataFlavor.stringFlavor.equals(flavors[i])) 
            {
                return true;
            }
        }
        
        return false;
    }
}