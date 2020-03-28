package mchorse.metamorph.api.creative;

public class UserCategory extends MorphCategory
{
	public UserCategory(MorphSection parent, String title)
	{
		super(parent, title);
	}

	@Override
	public boolean isHidden()
	{
		return false;
	}
}