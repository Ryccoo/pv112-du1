
varying vec3 position;
varying vec3 n;
varying vec2 tex_coord;

uniform sampler2D texture;

uniform vec4 render;

void main()
{
    vec3 l = normalize(gl_LightSource[0].position.xyz - position);

    vec4 diffuse = gl_LightSource[0].diffuse * gl_FrontMaterial.diffuse * max(0.0, dot(n,l));
    vec4 ambient = gl_LightSource[0].ambient * gl_FrontMaterial.ambient;
    
    // 
    vec3 v = normalize(-position);
    vec3 h = normalize(v + l);

    vec4 specular = gl_LightSource[0].specular * gl_FrontMaterial.specular * pow( max(0.0, dot(n,h)) , gl_FrontMaterial.shininess);

    vec4 tex = texture2D(texture, tex_coord);
    //vec4 rock = texture2D(stone_tex, tex_coord);
    //vec4 woodRock = mix(wood, rock, 0.5);

    gl_FragColor = ambient + diffuse * tex + specular;

    
}