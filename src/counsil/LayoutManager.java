/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package counsil;

/**
 *
 * @author xminarik
 */
public interface LayoutManager {
    public void applyChanges();
    public void addToLayout(String title, String role);
    public void removeFromLayout(String title);

    public void delete(String requredProducer);

    public void add(String createConsumer, String string);
    

}
