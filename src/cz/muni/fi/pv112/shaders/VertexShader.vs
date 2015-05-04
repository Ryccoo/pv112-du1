/* Built-in variables
*   
*   gl_Vertex - pociatocna pozicia bodu
*   gl_ModelViewMatrix
*   gl_ModelViewProjectionMatrix
*   gl_NormalMatrix - inverzna transponovana gl_ModelViewMatrix
*   gl_LightSource[] - zdroj svetla
*   gl_FrontMaterial - material
*   gl_Position - transformovana pozicia bodu
*/
varying vec3 position;
varying vec3 n;
varying vec2 tex_coord;


void main()
{
    position = vec3(gl_ModelViewMatrix * gl_Vertex);
    n = normalize(gl_NormalMatrix * gl_Normal);
    tex_coord = gl_MultiTexCoord0.st;

    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
}