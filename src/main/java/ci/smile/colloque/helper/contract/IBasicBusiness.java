package ci.smile.colloque.helper.contract;

import java.util.Locale;

public interface IBasicBusiness<T,K> {

	public abstract K create(T request,Locale locale);
	public abstract K getByCriteria(T request,Locale locale);
	public abstract K  update(T request,Locale locale);
	public abstract K delete(T request,Locale locale);
	
	

}
