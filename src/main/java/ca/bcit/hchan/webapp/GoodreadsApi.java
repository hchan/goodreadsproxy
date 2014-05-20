package ca.bcit.hchan.webapp;

import org.scribe.builder.api.DefaultApi10a;
import org.scribe.model.*;

import java.util.*;

public class GoodreadsApi extends DefaultApi10a
{
  public static final String AUTHORIZE_URL = "https://www.goodreads.com/oauth/authorize?oauth_token=%s";
  public static final String REQUEST_TOKEN_URL = "http://www.goodreads.com/oauth/request_token";
  public static final String ACCESS_TOKEN_URL = "http://www.goodreads.com/oauth/access_token";
  
  private final Set<String> scopes;

  public GoodreadsApi()
  {
    scopes = Collections.emptySet();
  }

  public GoodreadsApi(Set<String> scopes)
  {
    this.scopes = Collections.unmodifiableSet(scopes);
  }

  @Override
  public String getAccessTokenEndpoint()
  {
    return ACCESS_TOKEN_URL;
  }

  @Override
  public String getRequestTokenEndpoint()
  {
    return scopes.isEmpty() ? REQUEST_TOKEN_URL : REQUEST_TOKEN_URL + "?scope=" + scopesAsString();
  }

  private String scopesAsString()
  {
    StringBuilder builder = new StringBuilder();
    for(String scope : scopes)
    {
      builder.append("+" + scope);
    }
    return builder.substring(1);
  }

  @Override
  public String getAuthorizationUrl(Token requestToken)
  {
    return String.format(AUTHORIZE_URL, requestToken.getToken());
  }

  public static GoodreadsApi withScopes(String... scopes)
  {
    Set<String> scopeSet = new HashSet<String>(Arrays.asList(scopes));
    return new GoodreadsApi(scopeSet);
  }
  
  
}
